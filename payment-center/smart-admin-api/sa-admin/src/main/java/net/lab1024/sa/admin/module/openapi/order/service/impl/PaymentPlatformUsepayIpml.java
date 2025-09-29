package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderUsepayRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderUsepayRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.util.RSAUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bradydreamer
 * @date 2025/01/09 00:49
 */
@Slf4j
@Service
public class PaymentPlatformUsepayIpml implements IPaymentPlatformService {

    private static final String ORDER_AMOUNT_FORMAT = "####.00";

    private static final String PAYMENT_NOTIFY_URI = "/notify/usepay/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderUsepayRecordManager orderUsepayRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        //组装ciphertext
        Map<String, Object> params = new HashMap<>();
        params.put("payid", "1001");
        params.put("requestid", depositForm.getOrderNo());
        params.put("amount", depositForm.getAmount());
        params.put("callfront", depositForm.getLandingUrl());
        params.put("callback", domain + PAYMENT_NOTIFY_URI);
        params.put("customername", depositForm.getDepositHolder());
        params.put("paycard", depositForm.getBankAccount());

        Map<String, Object> postData = new HashMap<>();
        postData.put("access_token", channelInfoVO.getMerAk());
        postData.put("ciphertext", getCipherText(params, channelInfoVO.getMerSk()));

        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/pub/pay/records/gateway", postData, null);
        log.info("usepay request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("usepay apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"IN_PROGRESS".equals(status)) {
            log.error("payrock apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            orderUsepayRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);
            throw new BusinessException("Order application failed");
        }
        OrderUsepayRecordEntity sudingRecordEntity = orderUsepayRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(sudingRecordEntity.getReference());
        resultVO.setPaymentUrl(sudingRecordEntity.getPaymentUrl());
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }


    @Override
    public PaymentApplyResultVO applyForWithdraw(PaymentChannelInfoVO paymentChannelInfoVO, OrderWithdrawForm form, String subOrderNo) {
        return null;
    }

    @Override
    public OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    @Override
    public OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return null;
    }

    @Override
    public ReceiverInfoVO getReceiverInfo(String orderNo) {
        LambdaQueryWrapper<OrderUsepayRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUsepayRecordEntity::getOrderNo, orderNo);
        OrderUsepayRecordEntity recordEntity = orderUsepayRecordManager.getOne(queryWrapper);

        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.payrock);
        receiverInfoVO.setPageUrl(recordEntity.getPaymentUrl());
        return receiverInfoVO;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = (String) params.get("signature");

        String version = (String) params.get("version");
        String merchantCode = channelInfoVO.getMerAk();
        String cartId = params.get("cartId").toString();
        String orderAmount = formatAmount(new BigDecimal(params.get("amount").toString()));
        String currency = params.get("currency").toString();
        String status = params.get("status").toString();
        String merchantKey = channelInfoVO.getMerSk();

//        String checkSign = genSign(version, merchantCode, cartId, orderAmount, currency, status, merchantKey);
//        return checkSign.equals(sign);
        return true;
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("cartId"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

        Map<String, Object> params = new HashMap<>();
        params.put("requestid", orderNo);

        Map<String, Object> postData = new HashMap<>();
        postData.put("access_token", channelInfoVO.getMerAk());
        postData.put("ciphertext", getCipherText(params, channelInfoVO.getMerSk()));

        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/api/v1/payin/status", postData, null);
        log.info("usepay order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("usepay order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Boolean code = resultObj.getBoolean("code");
        if (!code) {
            log.error("usepay order detail error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        String data = resultObj.getString("data");
        Map<String, Object> dataJson = getClearParams(data, channelInfoVO.getMerSk());


        orderUsepayRecordManager.updateRecord(orderNo, dataJson);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(dataJson.get("paytime").toString());

        String payStatus = dataJson.get("paystatus").toString();

        if ("1".equals(payStatus)) {
            orderDetailVO.setStatus(2);
        } else if ("-1".equals(payStatus)) {
            orderDetailVO.setStatus(3);
        } else if ("-2".equals(payStatus)) {
            orderDetailVO.setStatus(3);
        } else if ("-3".equals(payStatus)) {
            orderDetailVO.setStatus(3);
        } else if ("-4".equals(payStatus)) {
            orderDetailVO.setStatus(3);
        } else {
            orderDetailVO.setStatus(1);
        }
        return orderDetailVO;
    }

    @Override
    public void paymentReceipt(PaymentChannelInfoEntity channelInfoVO, String orderNo, MultipartFile file) {
    }

    @Override
    public PaymentApplyResultVO paymentSwitch(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    @Override
    public void paymentCancel(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

    }

    private String getQueryString(Map<String, Object> params) {
        Map<String, Object> map = IPaymentPlatformService.filterParams(params);
        List<String> keyList = map.keySet().stream().sorted().collect(Collectors.toList());
        return keyList.stream().map(v -> v + "=" + map.get(v)).collect(Collectors.joining("&"));
    }

    private static String formatAmount(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat(ORDER_AMOUNT_FORMAT);
        return df.format(amount);
    }

    private static String getCipherText(Map<String, Object> params, String sk) {
        byte[] order_info_byte = JSON.toJSONString(params).getBytes(StandardCharsets.UTF_8);
        String ciphertext = RSAUtil.publicEncrypt(order_info_byte, sk);
        if (ciphertext == null) {
            log.error("usepay generate ciphertext failed!!!");
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        return ciphertext;
    }

    private static Map<String, Object> getClearParams(String cipherText, String sk) {
        try {
            byte[] cipherTextByte = Base64.getDecoder().decode(cipherText);
            String decryptStr = RSAUtil.privateDecrypt(cipherTextByte, sk);
            return JSON.parseObject(decryptStr);
        } catch (Exception e) {
            log.error("usepay decrypt ciphertext failed!!!");
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

}
