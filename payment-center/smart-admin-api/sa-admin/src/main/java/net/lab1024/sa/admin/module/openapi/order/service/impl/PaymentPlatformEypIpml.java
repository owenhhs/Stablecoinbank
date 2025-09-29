package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.EypPaymentFlagEnum;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderEypRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderEypRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformEypIpml implements IPaymentPlatformService {

    private static final String PAYMENT_NOTIFY_URI = "/notify/eyp/payment/order/result";

    private static final String CASH_NOTIFY_URI = "/notify/eyp/cash/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderEypRecordManager eypRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        String paymentMethod;
        if (StringUtils.isEmpty(depositForm.getPaymentChannel())) {
            paymentMethod = EypPaymentFlagEnum.valueOf(depositForm.getDepositType()).getPaymentFlag();
        } else {
            paymentMethod = EypPaymentFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("merchant_serial", channelInfoVO.getMerAk());
        params.put("merchant_order_no", depositForm.getOrderNo());
        params.put("channel_type", paymentMethod);
        params.put("payer_name", depositForm.getDepositHolder());
        params.put("amount", depositForm.getAmount());
        params.put("format", "JSON");
        params.put("random_string", DigestUtils.md5Hex(depositForm.getOrderNo()));
        params.put("notify_url", depositForm.getLandingUrl());

        String sign = sign(channelInfoVO.getMerSk(), params);

        params.put("callback_url", domain + PAYMENT_NOTIFY_URI);
        params.put("note", depositForm.getDepositRemark());
        params.put("sign", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/v1/collectApi/place", params, null);

        log.info("EYP request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EYP apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("EYP apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            eypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);
            throw new BusinessException("Order application failed");
        }
        OrderEypRecordEntity eypRecordEntity = eypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(eypRecordEntity.getOrderNo());
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }


    @Override
    public ReceiverInfoVO getReceiverInfo(String orderNo) {
        LambdaQueryWrapper<OrderEypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEypRecordEntity::getOrderNo, orderNo);
        OrderEypRecordEntity recordEntity = eypRecordManager.getOne(queryWrapper);

        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.eyp);
        receiverInfoVO.setBankAccount(recordEntity.getBankCard());
        receiverInfoVO.setBankAccountName(recordEntity.getBankName());
        receiverInfoVO.setBankName(recordEntity.getBankBranch());

        return receiverInfoVO;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = (String) params.get("sign");
        params.remove("payer_name");
        params.remove("sign");
        String checkSign = sign(channelInfoVO.getMerSk(), params);
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("merchant_order_no"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

        Map<String, Object> params = new HashMap<>();
        params.put("merchant_serial", channelInfoVO.getMerAk());
        params.put("merchant_order_no", orderNo);
        params.put("random_string", DigestUtils.md5Hex(orderNo));
        String sign = sign(channelInfoVO.getMerSk(), params);
        params.put("sign", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/v1/collectApi/check", params, null);
        log.info("EYP order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EYP order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        String amount = resultObj.getString("real_amount");
        String endtime = resultObj.getString("succeeded_at");
        eypRecordManager.updateRecord(orderNo, status, amount, endtime);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(endtime);

        if ("paid".equals(status) || "completed".equals(status)) {
            orderDetailVO.setStatus(2);
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

    @Override
    public PaymentApplyResultVO applyForWithdraw(PaymentChannelInfoVO paymentChannelInfoVO, OrderWithdrawForm form, String subOrderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_serial", paymentChannelInfoVO.getMerAk());
        params.put("merchant_order_no", subOrderNo);
        params.put("amount", form.getAmount());
        params.put("bank_card_no", form.getBankAccount());
        params.put("bank_holder", form.getAccountHolder());
        params.put("bank_name", form.getBankName());
        params.put("bank_branch", form.getBankBranch());
        params.put("bank_province", form.getBankProvince());
        params.put("bank_city", form.getBankCity());
        params.put("notify_url", domain + CASH_NOTIFY_URI);

        String sign = sign(paymentChannelInfoVO.getMerSk(), params);
        params.put("sign", sign);

        String resultStr = HttpUtil.apiPost(paymentChannelInfoVO.getDomain(), "/v1/payApi/place", params, null);
        log.info("EYP cash request, mercode:{}, params:{}, result:{}", paymentChannelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EYP cash result is empty, mercode:{}, result:{}", paymentChannelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Boolean success = resultObj.getBoolean("success");
        if (!success) {
            log.error("EYP cash error, mercode:{}, result:{}, params:{}", paymentChannelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            eypRecordManager.save(paymentChannelInfoVO, params, resultObj, PaymentTypeEnum.cash);
            throw new BusinessException("cash fail");
        }
        OrderEypRecordEntity eypRecordEntity = eypRecordManager.save(paymentChannelInfoVO, params, resultObj, PaymentTypeEnum.cash);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(eypRecordEntity.getPlatformOrderNo());
        return resultVO;
    }

    @Override
    public OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_serial", channelInfoVO.getMerAk());
        params.put("merchant_order_no", orderNo);
        String sign = sign(channelInfoVO.getMerSk(), params);
        params.put("sign", sign);
        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/v1/payApi/check", params, null);
        log.info("EYP order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EYP order detail result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("EYP order detail error , mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
        }
        int refCode = resultObj.getInteger("ref_code");

        String amount = resultObj.getString("amount");
        String endtime = resultObj.getString("succeeded_at");
        eypRecordManager.updateRecord(orderNo, String.valueOf(refCode), amount, endtime);

        OrderWithdrawDetailVO detailVO = new OrderWithdrawDetailVO();
        detailVO.setEndtime(endtime);
        WithdrawSubOrderStatusEnum statusEnum;
        if (refCode <= 3) {
            statusEnum = WithdrawSubOrderStatusEnum.PROCESSING;
        } else if (refCode == 5) {
            statusEnum = WithdrawSubOrderStatusEnum.SUCCESSED;
        } else {
            statusEnum = WithdrawSubOrderStatusEnum.FAILED;
        }
        detailVO.setStatus(statusEnum);
        return detailVO;
    }

    @Override
    public OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getWithdrawOrderDetail(channelInfoVO, (String) params.get("merchant_order_no"));
    }

    public static String sign(String apiSecret, Object payload) {
        try {
            String queryString = makeQueryString(apiSecret, payload);
            String encryption = DigestUtils.sha256Hex(queryString);
            String sign = DigestUtils.md5Hex(encryption);
            return sign.toUpperCase();
        } catch (Exception e) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

    private static String makeQueryString(String apiSecret, Object object) throws IllegalAccessException {
        Class<?> objectClass = object.getClass();
        StringBuilder sb = new StringBuilder();
        List<Field> fields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));
        fields.sort((field1, filed2) -> {
            String key1 = field1.getName();
            String key2 = filed2.getName();
            int comparison;
            int c1, c2;
            for (int index = 0; index < key1.length() && index < key2.length(); index++) {
                c1 = key1.toLowerCase().charAt(index);
                c2 = key2.toLowerCase().charAt(index);
                comparison = c1 - c2;
                if (comparison != 0) {
                    return comparison;
                }
            }
            return Integer.compare(key1.length(), key2.length());
        });
        for (Field field : fields) {
            String key = field.getName();
            Object value = field.get(object);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append("api_key=").append(apiSecret);
        return sb.toString();
    }
}
