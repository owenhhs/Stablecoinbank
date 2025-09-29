package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.emumeration.PayrockPaymentFlagEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderPayrockRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderPayrockRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformPayrockIpml implements IPaymentPlatformService {

    private static final String ORDER_AMOUNT_FORMAT = "####.00";

    private static final String TOKEN_DELIMITER = ";";

    private static final String PAYMENT_NOTIFY_URI = "/notify/payrock/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderPayrockRecordManager payrockRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        String paymentMethod = PayrockPaymentFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag();

        Map<String, Object> params = new HashMap<>();
        params.put("merchantCode", channelInfoVO.getMerAk());
        params.put("amount", depositForm.getAmount());
        params.put("callbackUrl", domain + PAYMENT_NOTIFY_URI);
        params.put("returnUrl", depositForm.getLandingUrl());
        params.put("currency", depositForm.getCurrency());
        params.put("country", depositForm.getCountry());
        params.put("cartId", depositForm.getOrderNo());
        params.put("paymentMethod", paymentMethod);
        params.put("ip", depositForm.getClientIp());
        params.put("version", "1.0");
        params.put("firstName", depositForm.getDepositHolder());
//        params.put("lastName", depositForm.getDepositHolder());
        params.put("email", StringUtils.isEmpty(depositForm.getEmail()) ? "user@user.com" : depositForm.getEmail());
//        Map<String, String> map = new HashMap<>();
//        map.put("city", "Beijing");
//        map.put("address", "Chaoyang");
//        map.put("postalCode", "1000000");
//        map.put("mobileNumber", "18600000001");
//        params.put("map", map);

        String sign = genSign(channelInfoVO.getMerAk(), channelInfoVO.getMerSk(), depositForm.getOrderNo(), paymentMethod,
                formatAmount(depositForm.getAmount()), depositForm.getCurrency(), depositForm.getCountry());
        params.put("hash", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/api/v1/payin", params, null);

        log.info("payrock request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(),  params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("payrock apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"IN_PROGRESS".equals(status)) {
            log.error("payrock apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            payrockRecordManager.save(channelInfoVO, params, resultObj);
            throw new BusinessException("Order application failed");
        }
        OrderPayrockRecordEntity sudingRecordEntity = payrockRecordManager.save(channelInfoVO, params, resultObj);

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
        LambdaQueryWrapper<OrderPayrockRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPayrockRecordEntity::getOrderNo, orderNo);
        OrderPayrockRecordEntity recordEntity = payrockRecordManager.getOne(queryWrapper);

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

        String checkSign = genSign(version, merchantCode, cartId, orderAmount, currency, status, merchantKey);
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("cartId"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

        String version = "1.0";

        Map<String, Object> params = new HashMap<>();
        params.put("cartId", orderNo);
        params.put("version", version);
        params.put("merchantCode", channelInfoVO.getMerAk());

        String sign = genSign(channelInfoVO.getMerSk(), orderNo, version, channelInfoVO.getMerAk());
        params.put("hash", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/api/v1/payin/status", params, null);
        log.info("payrock order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("payrock order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        String message = resultObj.getString("message");

        String amount = resultObj.getString("amount");
        String addtime = resultObj.getString("createdDate");
        String endtime = resultObj.getString("completedDate");
        payrockRecordManager.updateRecord(orderNo, status, message, amount, addtime, endtime);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(endtime);

        if ("SUCCESS".equals(status)) {
            orderDetailVO.setStatus(2);
        } else if ("FAILED".equals(status)) {
            orderDetailVO.setStatus(3);
        } else if ("DECLINED".equals(status)) {
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

    private String genSign(String... args) {
        String concatString = StringUtils.join(args, TOKEN_DELIMITER);
        return DigestUtils.sha256Hex(concatString);
    }

    private static String formatAmount(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat(ORDER_AMOUNT_FORMAT);
        return df.format(amount);
    }
}
