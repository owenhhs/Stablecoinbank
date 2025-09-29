package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderFxmchRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderFxmchRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.LogIdUtil;
import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/09/13 00:02
 */
@Slf4j
@Service
public class PaymentPlatformFxmchImpl implements IPaymentPlatformService {

    private static final String PAYMENT_NOTIFY_URI = "/notify/fxmch/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderFxmchRecordManager fxmchRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {
        Map<String, Object> params = new HashMap<>(11);
        params.put("orderNo", depositForm.getOrderNo());
        params.put("depositType", depositForm.getDepositType());
        params.put("paymentChannel", depositForm.getPaymentChannel());
        params.put("currency", depositForm.getCurrency());
        params.put("country", depositForm.getCountry());
        params.put("amount", depositForm.getAmount());
        params.put("depositHolder", depositForm.getDepositHolder());
        params.put("bankAccount", depositForm.getBankAccount());
        params.put("depositRemark", depositForm.getDepositRemark());
        params.put("callback", domain + PAYMENT_NOTIFY_URI);
        params.put("landingUrl", depositForm.getLandingUrl());

        params.put("merCode", channelInfoVO.getMerCode());
        params.put("userId", depositForm.getUserId());

        String date = SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/order/v1/ly/commgr/deposit";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, params, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);
        headers.put(LogIdUtil.REQUEST_ID, LogIdUtil.getRequestId());

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), uri, params, headers);

        log.info("[orderNo:{}] fxmch request, mercode:{}, params:{}, result:{}", depositForm.getOrderNo(), channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("[orderNo:{}] fxmch apply result is empty, mercode:{}, result:{}", depositForm.getOrderNo(), channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 0) {
            log.error("[orderNo:{}] fxmch apply error, mercode:{}, result:{}", depositForm.getOrderNo(), channelInfoVO.getMerCode(), resultStr);
            // 保存流水
            fxmchRecordManager.save(channelInfoVO, depositForm, resultObj);
            throw new BusinessException("Order application failed 【" + msg + "】");
        }

        OrderFxmchRecordEntity recordEntity = fxmchRecordManager.save(channelInfoVO, depositForm, resultObj);
        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(recordEntity.getOrderNo());
        resultVO.setSubMerName(recordEntity.getSubMerName());
        resultVO.setNewCashier(true);
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        OrderFxmchRecordEntity recordEntity = fxmchRecordManager.getRecordByOrderNo(orderNo);

        PaymentOrderInfoVO paymentOrderInfoVO = new PaymentOrderInfoVO();
        paymentOrderInfoVO.setPaymentMethod(DepositTypeEnum.BANK.equalsValue(recordEntity.getDepositType()) ?
                recordEntity.getDepositType() : recordEntity.getPaymentChannel());

        paymentOrderInfoVO.setPaymentStatus(recordEntity.getStatus());
        // 订单信息
        if (DepositTypeEnum.BANK.equalsValue(recordEntity.getDepositType())) {
            PaymentOrderInfoVO.BankInfo bankInfo = new PaymentOrderInfoVO.BankInfo();
            bankInfo.setBankAccount(recordEntity.getUsername());
            bankInfo.setBankCardNo(recordEntity.getBankNo());
            bankInfo.setBankName(recordEntity.getBankInfo());
            bankInfo.setBankBranch(recordEntity.getBankName());
            paymentOrderInfoVO.setBankInfo(bankInfo);
        } else {
            PaymentOrderInfoVO.QrCodeInfo qrCodeInfo = new PaymentOrderInfoVO.QrCodeInfo();
            qrCodeInfo.setAccount(recordEntity.getBankNo());
            qrCodeInfo.setUsername(recordEntity.getUsername());
            qrCodeInfo.setQrcode(recordEntity.getPayurl());
            paymentOrderInfoVO.setQrCodeInfo(qrCodeInfo);
        }
        return paymentOrderInfoVO;
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
        OrderFxmchRecordEntity recordEntity = fxmchRecordManager.getRecordByOrderNo(orderNo);
        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.fxmch);

        receiverInfoVO.setPayUrl(recordEntity.getPayurl());
        receiverInfoVO.setBankAccount(recordEntity.getBankNo());
        receiverInfoVO.setBankBranch(recordEntity.getBankName());
        receiverInfoVO.setBankAccountName(recordEntity.getUsername());
        receiverInfoVO.setBankName(recordEntity.getBankInfo());
        return receiverInfoVO;

    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {

        String authorization = (String) params.get("authorization");
        String date = (String) params.get("date");
        String uri = (String) params.get("uri");
        String method = (String) params.get("method");

        String contentType = "application/json";
        if (StringUtils.isEmpty(uri)) {
            uri = "/notify/fxmch/payment/order/result";
        }
        if (StringUtils.isEmpty(method)) {
            method = "GET";
        }
        String signature = SignatureUtil.genSignature(method, uri, contentType, date, null, channelInfoVO.getMerSk());
        String checkAuthorization = channelInfoVO.getMerAk() + ":" + signature;
        return authorization.equals(checkAuthorization);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("orderNo"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);

        String date = SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/order/v1/ly/commgr/depositDetails";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, params, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), uri, params, headers);

        log.info("fxmch detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch detail result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 0) {
            log.error("fxmch detail error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException("Query order details failed 【" + msg + "】");
        }

        JSONObject data = resultObj.getJSONObject("data");
        Integer status = data.getInteger("status");
        if (status != null && status != 1) {
            fxmchRecordManager.updateByOrderNo(orderNo, status);
        }

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(data.getString("updateTime"));
        if (status != null) {
            if (status == 4) {
                orderDetailVO.setStatus(3);
            } else if (status == 5) {
                orderDetailVO.setStatus(4);
            } else {
                orderDetailVO.setStatus(status);
            }
        }
        return orderDetailVO;
    }

    @Override
    public void paymentReceipt(PaymentChannelInfoEntity channelInfoVO, String orderNo, MultipartFile file) {
        Map<String, Object> params = new HashMap<>();

        String date = SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/order/v1/ly/commgr/deposit/receipt";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, params, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);

        String url = channelInfoVO.getDomain() + uri + "?orderNo=" + orderNo;
        String resultStr = HttpUtil.uploadFile(url, file, params, headers);

        log.info("fxmch paymentReceipt request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch paymentReceipt result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 0) {
            log.error("fxmch paymentReceipt error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException("Query order details failed 【" + msg + "】");
        }

    }

    @Override
    public PaymentApplyResultVO paymentSwitch(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        Map<String, Object> params = new HashMap<>(9);
        params.put("orderNo", orderNo);

        String date = SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/order/v1/ly/commgr/deposit/switch";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, params, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);
        headers.put(LogIdUtil.REQUEST_ID, LogIdUtil.getRequestId());

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), uri, params, headers);

        log.info("fxmch paymentSwitch request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch paymentSwitch result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 0) {
            log.error("fxmch paymentSwitch error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            // 保存流水
            fxmchRecordManager.updateRecord(orderNo, resultObj);
            if (code == 40001) {
                throw new BusinessException("更换付款码过于频繁");
            }
            throw new BusinessException("更换付款码失败，请重试");
        }
        OrderFxmchRecordEntity recordEntity = fxmchRecordManager.updateRecord(orderNo, resultObj);
        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(recordEntity.getOrderNo());
        resultVO.setSubMerName(recordEntity.getSubMerName());
        return resultVO;
    }


    @Override
    public void paymentCancel(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        Map<String, Object> params = new HashMap<>(9);
        params.put("orderNo", orderNo);

        String date = SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/order/v1/ly/commgr/deposit/cancel";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, params, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);
        headers.put(LogIdUtil.REQUEST_ID, LogIdUtil.getRequestId());

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), uri, params, headers);

        log.info("fxmch paymentCancel request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch paymentCancel result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 0) {
            log.error("fxmch paymentCancel error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            // 保存流水
            throw new BusinessException("fxmch paymentCancel failed 【" + msg + "】");
        }
    }
}
