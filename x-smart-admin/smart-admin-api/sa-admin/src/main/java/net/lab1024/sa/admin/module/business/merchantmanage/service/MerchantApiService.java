package net.lab1024.sa.admin.module.business.merchantmanage.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.entity.MerchantManageEntity;
import net.lab1024.sa.admin.module.business.merchantmanage.manager.MerchantManageManager;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleParentEntity;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 孙宇
 * @date 2024/07/26 00:44
 */
@Slf4j
@Service
public class MerchantApiService {

    @Resource
    private MerchantManageManager merchantManageManager;

    @Value("${pay.center.domain}")
    private String domain;

    @Resource
    private FileService fileService;

    @Async
    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 6000, multiplier = 1.5))
    public void orderCallback(OrderInfoEntity orderInfoEntity) {
        try {
            String url = orderInfoEntity.getCallback();
            if (url.contains("?")) {
                url = url + "&orderNo=" + orderInfoEntity.getOrderNo() + "&status=" + orderInfoEntity.getStatus();
            } else {
                url = url + "?orderNo=" + orderInfoEntity.getOrderNo() + "&status=" + orderInfoEntity.getStatus();
            }
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);


            LambdaQueryWrapper<MerchantManageEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MerchantManageEntity::getMerNo, "AK20240725001033");
            MerchantManageEntity merchantManageEntity = merchantManageManager.getOne(queryWrapper);

            String contentType = "application/json";
            String date = SmartLocalDateUtil.getGMTDate();
            String signature = SignatureUtil.genSignature(request.getMethod(), request.getURI().getPath(), contentType, date, null, merchantManageEntity.getSecretKey());

            request.setHeader("Content-Type", contentType);
            request.setHeader("Date", date);
            request.setHeader("Authorization", merchantManageEntity.getMerNo() + ":" + signature);

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Integer code = jsonObject.getInteger("code");
            if (code != ResponseDTO.OK_CODE) {
                throw new RuntimeException("request fail, responseBody=" + responseBody);
            }
        } catch (Exception e) {
            log.error("订单回调异常，orderNo:{}, msg: {}", orderInfoEntity.getOrderNo(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 商户结算单推送
     * @return
     */
    @Async
    @Retryable(value = Exception.class, maxAttempts = 4, backoff = @Backoff(delay = 3000))
    public void orderSettlePush(PaymentSettleParentEntity parentEntity) {
        try {
            List<String> settleUrlList = new ArrayList<>();
            for (String fileId : parentEntity.getSettleUrl().split(",")) {
                ResponseDTO<String> responseDTO = fileService.getFileUrlById(Long.valueOf(fileId));
                if (!responseDTO.getOk()) {
                    throw new BusinessException(responseDTO.getMsg());
                }
                settleUrlList.add(responseDTO.getData());
            }
            String url = domain + "settleInfo/notifySettle";

            Map<String, Object> params = new HashMap<>();
            params.put("merCode", parentEntity.getMerCode());
            params.put("tradeDate", parentEntity.getTradeDate());
            params.put("tradeType", parentEntity.getTradeType() == 1 ? "payment" : "cash");
            params.put("tradeAmount", parentEntity.getAmount());
            params.put("orderCount", parentEntity.getCountAmount());
            params.put("currency", parentEntity.getCurrency());
            params.put("exchangeRate", parentEntity.getExchangeRate());
            params.put("brokerage", parentEntity.getMerBrokerage());
            params.put("award", parentEntity.getMerAward());
            params.put("settleAmount", parentEntity.getMerShouldSettled());
            params.put("settleUrls", settleUrlList);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);

            String postData = JSONObject.toJSONString(params);
            StringEntity stringEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            request.setEntity(stringEntity);

            LambdaQueryWrapper<MerchantManageEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MerchantManageEntity::getMerNo, "AK20240725001033");
            MerchantManageEntity merchantManageEntity = merchantManageManager.getOne(queryWrapper);

            String contentType = "application/json";
            String date = SmartLocalDateUtil.getGMTDate();
            String signature = SignatureUtil.genSignature(request.getMethod(), request.getURI().getPath(), contentType, date, null, merchantManageEntity.getSecretKey());

            request.setHeader("Content-Type", contentType);
            request.setHeader("Date", date);
            request.setHeader("Authorization", merchantManageEntity.getMerNo() + ":" + signature);

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Integer code = jsonObject.getInteger("code");
            if (code != ResponseDTO.OK_CODE) {
                throw new RuntimeException("request fail, responseBody=" + responseBody);
            }
        } catch (Exception e) {
            log.error("结算订单回调异常，orderNo:{}, msg: {}", parentEntity.getId(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 回调结算凭证
     */
    @Async
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public ResponseDTO<String> orderSettleUrl(Long settleId, List<String> fileIds, String tradeDate, String merCode) {
        try {
            List<String> settleUrlList = new ArrayList<>();
            for (String fileId : fileIds) {
                ResponseDTO<String> responseDTO = fileService.getFileUrlById(Long.valueOf(fileId));
                if (!responseDTO.getOk()) {
                    throw new BusinessException(responseDTO.getMsg());
                }
                settleUrlList.add(responseDTO.getData());
            }
            if (CollectionUtils.isEmpty(settleUrlList)) {
                log.info("orderSettle,获取附件url为空 fileIds:{}", fileIds);
                return ResponseDTO.ok();
            }
            String url = domain + "settleInfo/notifySettleUrl";

            Map<String, Object> params = new HashMap<>();
            params.put("tradeDate", tradeDate);
            params.put("merCode", merCode);
            params.put("settleUrls", settleUrlList);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);

            String postData = JSONObject.toJSONString(params);
            StringEntity stringEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            request.setEntity(stringEntity);

            LambdaQueryWrapper<MerchantManageEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MerchantManageEntity::getMerNo, "AK20240725001033");
            MerchantManageEntity merchantManageEntity = merchantManageManager.getOne(queryWrapper);

            String contentType = "application/json";
            String date = SmartLocalDateUtil.getGMTDate();
            String signature = SignatureUtil.genSignature(request.getMethod(), request.getURI().getPath(), contentType, date, null, merchantManageEntity.getSecretKey());

            request.setHeader("Content-Type", contentType);
            request.setHeader("Date", date);
            request.setHeader("Authorization", merchantManageEntity.getMerNo() + ":" + signature);

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Integer code = jsonObject.getInteger("code");
            if (code != ResponseDTO.OK_CODE) {
                throw new RuntimeException("request fail, responseBody=" + responseBody);
            }
        } catch (Exception e) {
            log.error("结算订单回调异常，orderNo:{}, msg: {}", settleId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return ResponseDTO.ok();
    }
}
