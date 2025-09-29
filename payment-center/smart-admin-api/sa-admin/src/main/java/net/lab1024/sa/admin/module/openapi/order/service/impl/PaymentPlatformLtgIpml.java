package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.emumeration.PayrockLtgFlagEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLtgRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderLtgRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class PaymentPlatformLtgIpml implements IPaymentPlatformService {

    private static final String PAYMENT_NOTIFY_URI = "/notify/ltg/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderLtgRecordManager ltgRecordManager;

    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        String paymentMethod = PayrockLtgFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag();

        LtgExtVo extMap = JSONObject.parseObject(channelInfoVO.getExt(), LtgExtVo.class);

        Map<String, Object> params = new HashMap<>();
        params.put("mchNo", channelInfoVO.getMerAk());
        params.put("amount", formatAmount(depositForm.getAmount()));
        params.put("ifCode", paymentMethod);
        params.put("apiInfo", extMap.getApiInfo());
        params.put("mchOrderNo", depositForm.getOrderNo());
        params.put("notifyUrl", domain + PAYMENT_NOTIFY_URI);
        params.put("subject", depositForm.getName());
        params.put("body", depositForm.getName());
        params.put("signType", "MD5");

//        String sign = genSign(params, channelInfoVO.getMerSk());
//        params.put("sign", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/api/pay/infoPayOrderApi", params, null);

        log.info("ltg request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(),  params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("ltg apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        if (code != 0) {
            log.error("ltg apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            ltgRecordManager.save(channelInfoVO, params, resultObj);
            throw new BusinessException("Order application failed");
        }
        OrderLtgRecordEntity sudingRecordEntity = ltgRecordManager.save(channelInfoVO, params, resultObj);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(sudingRecordEntity.getPayOrderId());
        resultVO.setNewCashier(true);
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        LambdaQueryWrapper<OrderLtgRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLtgRecordEntity::getOrderNo, orderNo);
        OrderLtgRecordEntity recordEntity = ltgRecordManager.getOne(queryWrapper);

        PaymentOrderInfoVO paymentOrderInfoVO = new PaymentOrderInfoVO();
        paymentOrderInfoVO.setPaymentMethod(PayrockLtgFlagEnum.Alipay.getValue());

        if (StringUtils.isEmpty(recordEntity.getOrderState())) {
            paymentOrderInfoVO.setPaymentStatus(1);
        } else if ("2".equals(recordEntity.getOrderState())) {
            paymentOrderInfoVO.setPaymentStatus(2);
        } else if (Integer.parseInt(recordEntity.getOrderState()) > 2) {
            paymentOrderInfoVO.setPaymentStatus(3);
        } else {
            paymentOrderInfoVO.setPaymentStatus(1);
        }
        // 订单信息
        PaymentOrderInfoVO.QrCodeInfo qrCodeInfo = new PaymentOrderInfoVO.QrCodeInfo();
        qrCodeInfo.setQrcode(recordEntity.getPayData());
        paymentOrderInfoVO.setQrCodeInfo(qrCodeInfo);
        return paymentOrderInfoVO;
    }


    @Override
    public ReceiverInfoVO getReceiverInfo(String orderNo) {
        LambdaQueryWrapper<OrderLtgRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLtgRecordEntity::getOrderNo, orderNo);
        OrderLtgRecordEntity recordEntity = ltgRecordManager.getOne(queryWrapper);

        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.ltg);
        receiverInfoVO.setImgUrl(recordEntity.getPayData());
        return receiverInfoVO;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = (String) params.get("sign");
        params.remove("sign");
        String merchantKey = channelInfoVO.getMerSk();
        String checkSign = genSign(params, merchantKey);
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("mchOrderNo"),  (String) params.get("appId"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo, String appId) {
        LtgExtVo extMap = JSONObject.parseObject(channelInfoVO.getExt(), LtgExtVo.class);
        String appKey = extMap.getAppAkSk().get(appId);
        if (StringUtils.isEmpty(appKey)) {
            log.error("LTG 配置中不存在该渠道：{}", appId);
            throw new BusinessException(OpenErrorCode.LTG_CHANNEL_ERROR);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("mchNo", channelInfoVO.getMerAk());
        params.put("appId", appId);
        params.put("apiInfo", extMap.getApiInfo());
        params.put("mchOrderNo", orderNo);
        params.put("reqTime", System.currentTimeMillis());
        params.put("version", "1.0");
        params.put("signType", "MD5");

        String sign = genSign(params, appKey);
        params.put("sign", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/api/pay/queryApi", params, null);
        log.info("ltg order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("ltg order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        int code = resultObj.getInteger("code");
        if (code != 0) {
            log.error("ltg query error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            throw new BusinessException("Query order info failed");
        }

        JSONObject data = resultObj.getJSONObject("data");

        int status = data.getInteger("state");
        long amount = data.getLongValue("amount");
        String addtime = data.getString("createdAt");
        String endtime = data.getString("successTime");
        ltgRecordManager.updateRecord(orderNo, String.valueOf(status), amount, addtime, endtime, appId);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(endtime);
        if (status == 2) {
            orderDetailVO.setStatus(2);
        } else if (status > 2) {
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


    private String genSign(Map<String, Object> params, String sk) {
        String query = joinQueryParams(params);
        String concatString = query + "&key=" + sk;
        return DigestUtils.md5Hex(concatString).toUpperCase();
    }

    private static long formatAmount(BigDecimal amount) {
        // 乘以100得到分
        BigDecimal fen = amount.multiply(new BigDecimal("100"));
        // 转换为整数分
        return fen.setScale(0, RoundingMode.UNNECESSARY).longValueExact();
    }


    private static String joinQueryParams(Object body) {
        if (body == null) {
            return "";
        }
        Map<String, Object> params;
        if (body instanceof Map) {
            params = (Map<String, Object>) body;
            if (params.isEmpty()) {
                return "";
            }
        } else {
            Class<?> clazz = body.getClass();
            // 获取所有属性
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length == 0) {
                return "";
            }
            // 遍历并打印属性信息
            params = new HashMap<>(fields.length);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = ReflectionUtil.getFieldValue(field, body);
                if (value == null) {
                    continue;
                }
                if (value instanceof String && StringUtils.isEmpty((String) value)) {
                    continue;
                }
                params.put(field.getName(), value);
            }
        }
        List<String> keyList = params.keySet().stream().sorted().collect(Collectors.toList());
        return keyList.stream().map(v -> v + "=" + params.get(v)).collect(Collectors.joining("&"));
    }
}
