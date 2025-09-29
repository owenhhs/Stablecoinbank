package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLypRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderLypRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformLypIpml implements IPaymentPlatformService {

    @Resource
    private OrderLypRecordManager lypRecordManager;
    @Resource
    private HttpServletRequest request;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {
        Map<String, Object> params = new HashMap<>();
        params.put("depositType", depositForm.getDepositType());
        params.put("paymentChannel", depositForm.getPaymentChannel());
        params.put("amount", depositForm.getAmount());
        params.put("depositHolder", depositForm.getDepositHolder());
        params.put("depositRemark", depositForm.getDepositRemark());
        params.put("orderNo", depositForm.getOrderNo());

        Map<String, String> headers = new HashMap<>();
        String nonce = RandomStringUtils.randomAlphanumeric(14);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = getSign(channelInfoVO.getMerAk(), channelInfoVO.getMerSk(), JSONObject.toJSONString(params), nonce, timestamp);
        headers.put("appKey", channelInfoVO.getMerAk());
        headers.put("nonce", nonce);
        headers.put("timestamp", timestamp);
        headers.put("signature", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/order/v2/ly/commgr/deposit", params, headers);

        log.info("LYP request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("LYP apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(200)) {
            log.error("LYP apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            lypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);
            throw new BusinessException("Order application failed");
        }
        OrderLypRecordEntity eypRecordEntity = lypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.payment);

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
        LambdaQueryWrapper<OrderLypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLypRecordEntity::getOrderNo, orderNo);
        OrderLypRecordEntity recordEntity = lypRecordManager.getOne(queryWrapper);

        ReceiverInfoVO receiverInfoVO = new ReceiverInfoVO();
        receiverInfoVO.setChannel(PaymentPlatformEnum.lyp);
        receiverInfoVO.setBankAccount(recordEntity.getAccount());
        receiverInfoVO.setBankAccountName(recordEntity.getAccountName());
        receiverInfoVO.setBankName(recordEntity.getBankName());
        receiverInfoVO.setBankBranch(recordEntity.getBankBranch());
        return receiverInfoVO;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        String sign = request.getHeader("signature");
        String checkSign = sha512HmacBase64(channelInfoVO.getMerSk(), JSONObject.toJSONString(params));
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getPaymentOrderDetail(channelInfoVO, (String) params.get("orderNo"));
    }

    @Override
    public OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {

        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);

        Map<String, String> headers = new HashMap<>();
        String nonce = RandomStringUtils.randomAlphanumeric(14);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = getSign(channelInfoVO.getMerAk(), channelInfoVO.getMerSk(), JSONObject.toJSONString(params), nonce, timestamp);
        headers.put("appKey", channelInfoVO.getMerAk());
        headers.put("nonce", nonce);
        headers.put("timestamp", timestamp);
        headers.put("signature", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/order/v2/ly/commgr/depositDetails", params, headers);
        log.info("LYP order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("LYP order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(200)) {
            log.error("LYP order detail result error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject dataObj = resultObj.getJSONObject("data");

        Integer status = dataObj.getInteger("status");
        String result = resultObj.getString("result");
        String endtime = resultObj.getString("updateTime");
        lypRecordManager.updateRecord(orderNo, status, result);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setEndtime(endtime);

        if (status.equals(3)) {
            orderDetailVO.setStatus(2);
        } else if (status.equals(4) || status.equals(5)) {
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
    public PaymentApplyResultVO applyForWithdraw(PaymentChannelInfoVO channelInfoVO, OrderWithdrawForm form, String subOrderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("receiver", form.getAccountHolder());
        params.put("receivingAccount", form.getBankAccount());
        params.put("receivingBank", form.getBankName());
        params.put("receivingBranch", form.getBankBranch());
        params.put("amount", form.getAmount());
        params.put("currency", form.getCurrency());
        params.put("orderNo", subOrderNo);

        Map<String, String> headers = new HashMap<>();
        String nonce = RandomStringUtils.randomAlphanumeric(14);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = getSign(channelInfoVO.getMerAk(), channelInfoVO.getMerSk(), JSONObject.toJSONString(params), nonce, timestamp);
        headers.put("appKey", channelInfoVO.getMerAk());
        headers.put("nonce", nonce);
        headers.put("timestamp", timestamp);
        headers.put("signature", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/order/v2/ly/commgr/appWithdraw", params, headers);

        log.info("LYP cash request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("LYP cash result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(200)) {
            log.error("LYP cash error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            lypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.cash);
            throw new BusinessException("Order application failed");
        }
        OrderLypRecordEntity eypRecordEntity = lypRecordManager.save(channelInfoVO, params, resultObj, PaymentTypeEnum.cash);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(eypRecordEntity.getOrderNo());
        return resultVO;
    }

    @Override
    public OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);

        Map<String, String> headers = new HashMap<>();
        String nonce = RandomStringUtils.randomAlphanumeric(14);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = getSign(channelInfoVO.getMerAk(), channelInfoVO.getMerSk(), JSONObject.toJSONString(params), nonce, timestamp);
        headers.put("appKey", channelInfoVO.getMerAk());
        headers.put("nonce", nonce);
        headers.put("timestamp", timestamp);
        headers.put("signature", sign);

        String resultStr = HttpUtil.apiPost(channelInfoVO.getDomain(), "/order/v2/ly/commgr/orderDetails", params, headers);
        log.info("LYP cash order detail request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("LYP cash order detail result is empty,  mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(200)) {
            log.error("LYP cash order detail result error, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject dataObj = resultObj.getJSONObject("data");

        Integer status = dataObj.getInteger("status");
        String result = resultObj.getString("result");
        String endtime = resultObj.getString("updateTime");
        lypRecordManager.updateRecord(orderNo, status, result);

        OrderWithdrawDetailVO orderDetailVO = new OrderWithdrawDetailVO();
        orderDetailVO.setEndtime(endtime);

        if (status.equals(3)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.SUCCESSED);
        } else if (status.equals(4) || status.equals(5)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.FAILED);
        } else {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.PROCESSING);
        }
        return orderDetailVO;
    }

    @Override
    public OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        return getWithdrawOrderDetail(channelInfoVO, (String) params.get("orderNo"));
    }

    public static String sha512HmacBase64(String secret, String message) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha512Hmac.init(secretKey);
            byte[] hash = sha512Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
   }

    public static String getSign(String appKey, String secret, String data, String nonce, String timestamp) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(appKey);
        stringBuffer.append(secret);
        if (null != data) {
            stringBuffer.append(data);
        }
        stringBuffer.append(nonce);
        stringBuffer.append(timestamp);
        stringBuffer.append("verify");
        return DigestUtils.md5Hex(stringBuffer.toString());
    }
}
