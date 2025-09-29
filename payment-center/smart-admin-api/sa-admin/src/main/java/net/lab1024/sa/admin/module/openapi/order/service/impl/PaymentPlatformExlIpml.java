package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PayrockExlFlagEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderExlRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderExlRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformExlIpml implements IPaymentPlatformService {

    @Resource
    private OrderExlRecordManager exlRecordManager;

    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        String paymentMethod = PayrockExlFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag();

        Map<String, Object> params = new HashMap<>();
        params.put("uid", channelInfoVO.getMerAk());
        params.put("payType", paymentMethod);
        params.put("uniqueCode", depositForm.getUserId());
        params.put("money", depositForm.getAmount());
        params.put("orderId", depositForm.getOrderNo());
        params.put("payerName", depositForm.getDepositHolder());
        params.put("jumpUrl", depositForm.getLandingUrl());

        String sign = sign(channelInfoVO.getMerSk(), params);
        params.put("signature", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/coin/pay/order/pay/checkout/counter", params, null);

        log.info("EXL request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EXL apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("EXL apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            exlRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);
            throw new BusinessException("Order application failed");
        }
        OrderExlRecordEntity eypRecordEntity = exlRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(eypRecordEntity.getOrderNo());
        resultVO.setPaymentUrl(eypRecordEntity.getPayUrl());
        return resultVO;
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }


    @Override
    public ReceiverInfoVO getReceiverInfo(String orderNo) {
        return null;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = (String) params.get("signature");
        params.remove("signature");
        String checkSign = sign(channelInfoVO.getMerSk(), params);
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        LambdaQueryWrapper<OrderExlRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderExlRecordEntity::getOrderNo, params.get("apiOrderNo"));
        OrderExlRecordEntity result = exlRecordManager.getOne(queryWrapper);

        String tradeStatus = params.get("tradeStatus").toString();
        String finishedTime = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);
        exlRecordManager.updateRecord(result.getOrderNo(), tradeStatus);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        if (StringUtils.isEmpty(tradeStatus)) {
            orderDetailVO.setStatus(2);
        } else if ("1".equals(tradeStatus)) {
            orderDetailVO.setStatus(3);
        } else if ("2".equals(tradeStatus)) {
            orderDetailVO.setStatus(1);
        }
        orderDetailVO.setEndtime(finishedTime);
        return orderDetailVO;
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
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
        params.put("uid", paymentChannelInfoVO.getMerAk());
        params.put("money", form.getAmount());
        params.put("orderId", subOrderNo);
        params.put("payerName", form.getAccountHolder());
        params.put("cardNo", form.getBankAccount());
        params.put("bankName", form.getBankName());

        String sign = sign(paymentChannelInfoVO.getMerSk(), params);
        params.put("signature", sign);

        String resultStr = HttpUtil.apiPost(paymentChannelInfoVO.getDomain(), "/coin/pay/order/pay/withdrawal", params, null);
        log.info("EXL cash request, mercode:{}, params:{}, result:{}", paymentChannelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("EXL cash result is empty, mercode:{}, result:{}", paymentChannelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(1)) {
            log.error("EXL cash error, mercode:{}, result:{}, params:{}", paymentChannelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            exlRecordManager.save(paymentChannelInfoVO, params, resultObj, PaymentTypeEnum.cash);
            throw new BusinessException("cash fail");
        }
        OrderExlRecordEntity exlRecordEntity = exlRecordManager.save(paymentChannelInfoVO, params, resultObj, PaymentTypeEnum.cash);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(exlRecordEntity.getOrderNo());
        return resultVO;
    }

    @Override
    public OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    @Override
    public OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        LambdaQueryWrapper<OrderExlRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderExlRecordEntity::getOrderNo, params.get("bill_no"));
        OrderExlRecordEntity result = exlRecordManager.getOne(queryWrapper);

        String tradeStatus = params.get("tradeStatus").toString();
        exlRecordManager.updateRecord(result.getOrderNo(), tradeStatus);

        OrderWithdrawDetailVO orderDetailVO = new OrderWithdrawDetailVO();
        if (StringUtils.isEmpty(tradeStatus)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.SUCCESSED);
        } else if ("1".equals(tradeStatus)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.FAILED);
        }
        return orderDetailVO;
    }

    public static String sign(String apiSecret, Map<String, Object> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            if (params.get(key) != null) {
                builder.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        builder.append("key=").append(apiSecret);
        return DigestUtils.md5Hex(builder.toString());
    }
}
