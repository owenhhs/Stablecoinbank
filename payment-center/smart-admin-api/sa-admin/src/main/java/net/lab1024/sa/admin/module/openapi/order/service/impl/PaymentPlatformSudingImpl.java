package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.NoahPaymentFlagEnum;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderSudingRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderSudingRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 孙宇
 * @date 2024/09/04 21:29
 */
@Slf4j
@Service
public class PaymentPlatformSudingImpl implements IPaymentPlatformService {

    private static final String PAYMENT_NOTIFY_URI = "/notify/noah/payment/order/result";

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private OrderSudingRecordManager orderSudingRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO paymentChannelInfoVO, OrderDepositForm depositForm) {
        Map<String, Object> params = new HashMap<>();
        params.put("cus_code", paymentChannelInfoVO.getMerAk());
        params.put("cus_order_sn", depositForm.getOrderNo());
        if (DepositTypeEnum.BANK.equalsValue(depositForm.getDepositType())) {
            params.put("payment_flag", NoahPaymentFlagEnum.bank.getPaymentFlag());
        } else {
            params.put("payment_flag", NoahPaymentFlagEnum.valueOf(depositForm.getPaymentChannel()).getPaymentFlag());
        }
        params.put("amount", depositForm.getAmount());
//        params.put("bank_code", depositForm.getBankCode());
        params.put("notify_url", domain + PAYMENT_NOTIFY_URI);
        params.put("redirect_url", depositForm.getLandingUrl());
        params.put("attach_data", depositForm.getExt());
        params.put("end_user_ip", depositForm.getClientIp());

        String queryString = getQueryString(params);
        String sign = genSign(queryString, paymentChannelInfoVO.getMerSk());

        queryString = queryString + "&sign=" + sign;

        String resultStr = HttpUtil.apiGet(paymentChannelInfoVO.getDomain(), "/api/payment/deposit?" + queryString, null);

        log.info("suding request, params:{}, result:{}", params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("suding apply result is empty,  result:{}", resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String result = resultObj.getString("result");
        int code = resultObj.getInteger("status");
        if (code != 200 || !"success".equals(result)) {
            log.error("suding apply error,  result:{}", resultStr);
            // 保存流水
            orderSudingRecordManager.save(paymentChannelInfoVO, params, resultObj);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        OrderSudingRecordEntity sudingRecordEntity = orderSudingRecordManager.save(paymentChannelInfoVO, params, resultObj);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(sudingRecordEntity.getOrderSn());
//        resultVO.setPageUrl(sudingRecordEntity.getPaymentUri());
//        resultVO.setImgUrl(sudingRecordEntity.getPaymentImg());
//
//        resultVO.setBankName(sudingRecordEntity.getBankName());
//        resultVO.setBankBranch(sudingRecordEntity.getBankBranch());
//        resultVO.setBankAccount(sudingRecordEntity.getBankAccount());
//        resultVO.setBankAccountName(sudingRecordEntity.getBankAccountName());
//        resultVO.setOwnerName(sudingRecordEntity.getOwnerName());
//        resultVO.setOwnerAccount(sudingRecordEntity.getOwnerAccount());
//        resultVO.setOwnerNickName(sudingRecordEntity.getOwnerNickName());
//        resultVO.setOwnerPhoneNumber(sudingRecordEntity.getOwnerPhoneNumber());
//        resultVO.setOwnerUrlLink(sudingRecordEntity.getOwnerUrlLink());
//        resultVO.setOwnerQrcode(sudingRecordEntity.getOwnerQrcode());
//        resultVO.setOwnerQrcodeUrl(sudingRecordEntity.getOwnerQrcodeUrl());

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
        LambdaQueryWrapper<OrderSudingRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSudingRecordEntity::getOrderSn, orderNo);
        OrderSudingRecordEntity recordEntity = orderSudingRecordManager.getOne(queryWrapper);
        ReceiverInfoVO receiverInfoVO = SmartBeanUtil.copy(recordEntity, ReceiverInfoVO.class);
        receiverInfoVO.setPayUrl(recordEntity.getPaymentUri());
        receiverInfoVO.setImgUrl(recordEntity.getPaymentImg());
        receiverInfoVO.setChannel(PaymentPlatformEnum.suding);
        return receiverInfoVO;
    }


    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {

        String sign = (String) params.get("sign");
        params.remove("sign");

        String queryString = getQueryString(params);
        String checkSign = genSign(queryString, channelInfoVO.getMerSk());
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {

        return null;
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


    private String getQueryString(Map<String, Object> params) {
        Map<String, Object> map = IPaymentPlatformService.filterParams(params);
        final List<String> keys = Arrays.asList("attach_data", "notify_url", "redirect_url");
        List<String> keyList = map.keySet().stream().sorted().collect(Collectors.toList());
        return keyList.stream().map(v -> {
            if (keys.contains(v)) {
                String encodeStr;
                try {
                    encodeStr = URLEncoder.encode((String) map.get(v), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return v + "=" + encodeStr;
            }
            return v + "=" + map.get(v);
        }).collect(Collectors.joining("&"));
    }


    private String genSign(String queryString, String sk) {
        return DigestUtils.md5Hex(queryString + "&key=" + sk);
    }





}
