package net.lab1024.sa.admin.module.openapi.order.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.merchant.domain.entity.MerchantManageEntity;
import net.lab1024.sa.base.module.support.merchant.manager.MerchantManageManager;
import net.lab1024.sa.admin.module.openapi.order.manager.PaymentOrderCallbackRecordManager;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.RedisLockUtil;
import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.constant.RedisKeyConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 孙宇
 * @date 2024/07/26 00:44
 */
@Slf4j
@Service
public class OrderPushService {
    @Resource
    private MerchantManageManager merchantManageManager;

    @Resource
    private PaymentOrderCallbackRecordManager paymentOrderCallbackRecordManager;

    private static final String RESULT_SUCCESS_OK = "u0022ok";

    @Async
    @Retryable(value = {RuntimeException.class, BusinessException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 10000, multiplier = 2, maxDelay = 20 * 60000))
    public void paymentOrderPush(Long platformId, String orderNo, Integer status, Integer paymentStatus, String callback, String ext) throws Exception {
        if (status.equals(1)) {
            return;
        }
        log.info("paymentOrderPush getlock, orderNo:{}, status:{}, callback:{}", orderNo, status, callback);
        String redisLock = RedisKeyConst.Order.ORDER_CALLBACK_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            log.error("订单回调触发频繁，orderNo:{}", orderNo);
            return;
        }
        log.info("paymentOrderPush getlock, orderNo:{}, status:{}, callback:{}", orderNo, status, callback);
        try {
            if (paymentOrderCallbackRecordManager.hasSuccessCallbackRecord(orderNo, status)) {
                log.info("订单回调记录已存在，不再进行回调， orderNo:{}", orderNo);
                return;
            }
            log.info("paymentOrderPush, orderNo:{}, status:{}, callback:{}", orderNo, status, callback);

            Map<String, Object> params = new HashMap<>();
            params.put("orderNo", orderNo);
            params.put("status", status);
            params.put("payStatus", paymentStatus);
//        params.put("endTime", orderInfoEntity.getFinishedTime());
            if (ext == null) {
                params.put("ext", "");
            } else {
                params.put("ext", ext);
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(callback);
            String contentType = "application/json";

            StringEntity entity = new StringEntity(JSONObject.toJSONString(params));
            request.setEntity(entity);
            request.setHeader("Content-Type", contentType);

            MerchantManageEntity merchant = merchantManageManager.getById(platformId);

            String date = SmartLocalDateUtil.getGMTDate();
            String signature = SignatureUtil.genSignature(request.getMethod(), request.getURI().getPath(), contentType, date, params, merchant.getSecretKey());

            request.setHeader("Date", date);
            request.setHeader("Authorization", merchant.getMerNo() + ":" + signature);

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo,status, 0, "http code:" + response.getStatusLine().getStatusCode());
                log.error("paymentOrderPush error, statusCode:{}, params:{}, responseBody:{}", response.getStatusLine().getStatusCode(), params, responseBody);
                throw new RuntimeException("paymentOrderPush error, statusCode:" + response.getStatusLine().getStatusCode() + ", responseBody:" + responseBody);
            }
            if (StringUtils.isEmpty(responseBody)) {
                paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo, status, 0, "response is empty");
                log.error("paymentOrderPush error, responseBody is empty");
                throw new RuntimeException("paymentOrderPush error, responseBody is empty");
            }

            log.info("支付订单回调推送 orderNo:{}, statusCode:{}, params:{}, responseBody:{}", orderNo, response.getStatusLine().getStatusCode(), params, responseBody);
            if (responseBody.contains(RESULT_SUCCESS_OK)) {
                paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo, status, 1, responseBody);
                return;
            }
            try {
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                Integer code = jsonObject.getInteger("code");
                if (code == null || ResponseDTO.OK_CODE != code) {
                    paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo, status, 0, responseBody);
                    log.error("request fail, params:{}, responseBody:{}", params, responseBody);
                    throw new BusinessException("request fail, responseBody=" + responseBody);
                }
                paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo, status, 1, responseBody);
            } catch (Exception e) {
                paymentOrderCallbackRecordManager.saveCallbackRecord(orderNo, status, 0, responseBody);
                log.error("支付订单回调推送异常 orderNo:{}, statusCode:{}, params:{}, responseBody:{}, errorMessage:{}", orderNo, response.getStatusLine().getStatusCode(), params, responseBody, e.getMessage());
                throw e;
            }
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }
}
