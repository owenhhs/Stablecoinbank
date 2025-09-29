package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.LetonggouPaymentFlagEnum;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLetonggouRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderLetonggouRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformLetonggouIpml implements IPaymentPlatformService {

    private static final String PAYMENT_NOTIFY_URI = "/notify/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderLetonggouRecordManager orderLetonggouRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        Map<String, Object> params = new HashMap<>();
        params.put("pid", channelInfoVO.getMerAk());
        params.put("type", LetonggouPaymentFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag());
        params.put("out_trade_no", depositForm.getOrderNo());
        params.put("name", depositForm.getName());
        params.put("money", depositForm.getAmount());
        params.put("notify_url", domain + PAYMENT_NOTIFY_URI);
        params.put("return_url", depositForm.getLandingUrl());
        params.put("clientip", depositForm.getClientIp());
        if (StringUtils.isNotBlank(depositForm.getDevice())) {
            params.put("device", depositForm.getDevice());
        }
        if (StringUtils.isNotEmpty(depositForm.getExt())) {
            params.put("param", depositForm.getExt());
        }

        String queryString = getQueryString(params);
        String sign = genSign(queryString, channelInfoVO.getMerSk());
        params.put("sign", sign);
        params.put("sign_type", "MD5");

        String resultStr = HttpUtil.apiPostForm(channelInfoVO.getDomain(), "/mapi.php", params, null);

        log.info("letonggou request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(),  params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("letonggou apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 1) {
            log.error("letonggou apply error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            // 保存流水
            orderLetonggouRecordManager.save(channelInfoVO, params, resultObj);
            throw new BusinessException("Order application failed 【" + msg + "】");
        }
        OrderLetonggouRecordEntity sudingRecordEntity = orderLetonggouRecordManager.save(channelInfoVO, params, resultObj);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(sudingRecordEntity.getTradeNo());
        resultVO.setNewCashier(true);
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        LambdaQueryWrapper<OrderLetonggouRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLetonggouRecordEntity::getOrderNo, orderNo);
        OrderLetonggouRecordEntity recordEntity = orderLetonggouRecordManager.getOne(queryWrapper);

        PaymentOrderInfoVO paymentOrderInfoVO = new PaymentOrderInfoVO();
        paymentOrderInfoVO.setPaymentMethod(LetonggouPaymentFlagEnum.getByFlag(recordEntity.getPaymentType()).getValue());

        if (recordEntity.getStatus() == 1) {
            paymentOrderInfoVO.setPaymentStatus(2);
        } else {
            paymentOrderInfoVO.setPaymentStatus(1);
        }
        // 订单信息
        PaymentOrderInfoVO.QrCodeInfo qrCodeInfo = new PaymentOrderInfoVO.QrCodeInfo();
        qrCodeInfo.setQrcodeContent(StringUtils.isEmpty(recordEntity.getPayurl()) ? recordEntity.getQrcode() : recordEntity.getPayurl());
        paymentOrderInfoVO.setQrCodeInfo(qrCodeInfo);
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
        LambdaQueryWrapper<OrderLetonggouRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLetonggouRecordEntity::getOrderNo, orderNo);
        OrderLetonggouRecordEntity recordEntity = orderLetonggouRecordManager.getOne(queryWrapper);

        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.letonggou);
        receiverInfoVO.setPageUrl(recordEntity.getPayurl());
        receiverInfoVO.setWechatUrl(recordEntity.getUrlscheme());
        receiverInfoVO.setOwnerQrcode(recordEntity.getQrcode());

        return receiverInfoVO;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = (String) params.get("sign");
        params.remove("sign");
        params.remove("sign_type");

        String queryString = getQueryString(params);
        String checkSign = genSign(queryString, channelInfoVO.getMerSk());
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("out_trade_no"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

        Map<String, Object> params = new HashMap<>();
        params.put("act", "order");
        params.put("pid", channelInfoVO.getMerAk());
        params.put("key", channelInfoVO.getMerSk());
        params.put("out_trade_no", orderNo);
        String queryString = getQueryString(params);

        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/api.php?" + queryString, null);
        log.info("letonggou order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("letonggou order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        String msg = resultObj.getString("msg");
        if (code != 1) {
            log.error("letonggou order detail error, mercode:{}, code:{}, msg:{}", channelInfoVO.getMerCode(), code, msg);
            // 保存流水
            throw new BusinessException("Query order details failed【" + msg + "】");
        }

        String apiTradeNo = resultObj.getString("api_trade_no");
        Integer status = resultObj.getInteger("status");
        String buyer = resultObj.getString("buyer");
        String addtime = resultObj.getString("addtime");
        String endtime = resultObj.getString("endtime");
        orderLetonggouRecordManager.updateRecord(orderNo, apiTradeNo, status, buyer, addtime, endtime);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(endtime);
        if (status == 1) {
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

    private String getQueryString(Map<String, Object> params) {
        Map<String, Object> map = IPaymentPlatformService.filterParams(params);
        List<String> keyList = map.keySet().stream().sorted().collect(Collectors.toList());
        return keyList.stream().map(v -> v + "=" + map.get(v)).collect(Collectors.joining("&"));
    }

    private String genSign(String queryString, String sk) {
        return DigestUtils.md5Hex(queryString + sk).toLowerCase();
    }
}
