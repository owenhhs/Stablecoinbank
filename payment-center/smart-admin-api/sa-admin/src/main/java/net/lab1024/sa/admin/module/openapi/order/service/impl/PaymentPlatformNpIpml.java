package net.lab1024.sa.admin.module.openapi.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderNpRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.manager.OrderNpRecordManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBigDecimalUtil;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 孙宇
 * @date 2024/09/06 00:49
 */
@Slf4j
@Service
public class PaymentPlatformNpIpml implements IPaymentPlatformService {

    @Resource
    private OrderNpRecordManager npRecordManager;


    @Override
    public PaymentApplyResultVO apply(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm) {

        Map<String, Object> params = new HashMap<>();
        params.put("sys_no", channelInfoVO.getMerAk());
        params.put("order_id", depositForm.getOrderNo());
        params.put("order_amount", SmartBigDecimalUtil.setScale(depositForm.getAmount(), 0));
        params.put("order_ip", depositForm.getClientIp());
        params.put("order_time", SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS));
        params.put("pay_user_name", depositForm.getDepositHolder());
        params.put("user_id", depositForm.getUserId());

        String sign = sign(channelInfoVO.getMerSk(), params);
        params.put("sign", sign);

        String resultStr = HttpUtil.apiPostForm(channelInfoVO.getDomain(), "/UtInRecordApi/orderGateWay", params, null);

        log.info("NP request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("NP apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("NP apply error, mercode:{}, result:{}, params:{}", channelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            // 保存流水
            npRecordManager.save(channelInfoVO, params, resultObj);
            throw new BusinessException("Order application failed");
        }
        OrderNpRecordEntity npRecordEntity = npRecordManager.save(channelInfoVO, params, resultObj);

        // 确认匹配订单
        confirmOrderName(channelInfoVO.getDomain(), npRecordEntity.getThirdOrderNo(), Long.valueOf(depositForm.getUserId()), depositForm.getDepositHolder());

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(npRecordEntity.getThirdOrderNo());
        resultVO.setNewCashier(true);
        return resultVO;
    }

    private void confirmOrderName(String domain, String thirdOrderNo, Long userId, String userName) {
        Map<String, Object> params = new HashMap<>();
        params.put("in_order_id", thirdOrderNo);
        params.put("user_id", userId);
        params.put("user_name", userName);
        params.put("bank_type", "100");
        params.put("device_type", "100");

        String resultStr = HttpUtil.apiGet(domain, "/UtInRecordApi/confirmOrderName", params, null);
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("NP 匹配订单失败，params:{}, result:{}", params, resultStr);
            throw new BusinessException("Order application failed");
        }
    }

    @Override
    public PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        OrderNpRecordEntity record = npRecordManager.getByOrderNo(orderNo);

        PaymentOrderInfoVO paymentOrderInfoVO = getPaymentOrderInfo(channelInfoVO, record.getThirdOrderNo(), record.getUserId());
        paymentOrderInfoVO.getOrderInfo().setAmount(record.getAmount());
        paymentOrderInfoVO.getOrderInfo().setPayer(record.getPayUserName());
        return paymentOrderInfoVO;
    }


    private PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String thirdOrderNo, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("in_order_id", thirdOrderNo);
        params.put("user_id", userId);
        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/UtInRecordApi/getInOrder", params, null);
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("NP 获取订单信息失败，params:{}, result:{}", params, resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }

        JSONObject data = resultObj.getJSONObject("data");
        Integer orderStatus = data.getInteger("status");

        PaymentOrderInfoVO paymentOrderInfoVO = new PaymentOrderInfoVO();
        paymentOrderInfoVO.setPaymentMethod("bank");

        // 100=创建订单，101=匹配中，102=匹配成功，等待商户付款，103=匹配失败，200=商户已付款，等待币商确认收款，300=订单已完成，990/991/992=订单已取消
        if (orderStatus <= 102) {
            paymentOrderInfoVO.setPaymentStatus(1);
            // 订单信息
            PaymentOrderInfoVO.OrderInfo orderInfo = new PaymentOrderInfoVO.OrderInfo();
            paymentOrderInfoVO.setOrderInfo(orderInfo);

            // 收款信息
            JSONObject bankInfoObj = data.getJSONObject("bank_info");
            // 存在已匹配未付款的订单，获取历史订单支付信息
            if (bankInfoObj == null) {
                JSONObject historyOrder = data.getJSONObject("history_order");
                return getPaymentOrderInfo(channelInfoVO, historyOrder.getString("order_no"), historyOrder.getString("user_id"));
            }
            JSONObject bankCardInfoObj = bankInfoObj.getJSONObject("bankcard");
            PaymentOrderInfoVO.BankInfo bankInfo = new PaymentOrderInfoVO.BankInfo();
            bankInfo.setBankAccount(bankCardInfoObj.getString("account_name"));
            bankInfo.setBankCardNo(bankCardInfoObj.getString("account_no"));
            bankInfo.setBankName(bankCardInfoObj.getString("bank_name"));
            bankInfo.setBankBranch(bankCardInfoObj.getString("account_address"));
            paymentOrderInfoVO.setBankInfo(bankInfo);
        } else if (orderStatus.equals(200) || orderStatus.equals(300)) {
            paymentOrderInfoVO.setPaymentStatus(2);
        } else {
            paymentOrderInfoVO.setPaymentStatus(3);
        }
        return paymentOrderInfoVO;
    }



    @Override
    public ReceiverInfoVO getReceiverInfo(String orderNo) {
        return null;
    }

    @Override
    public boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        NpExtVo extMap = JSONObject.parseObject(channelInfoVO.getExt(), NpExtVo.class);
        String sign = (String) params.get("sign");
        params.remove("sign");

        String content;

        if (params.containsKey("bill_status")) {
            content = joinQueryParams(params);
        } else {
            content = params.get("bill_no").toString();
        }
        String checkSign = callbackSign(extMap.getCallbackSecretkey(), content);
        return checkSign.equals(sign);
    }

    @Override
    public OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, params.get("bill_no"));
        OrderNpRecordEntity result = npRecordManager.getOne(queryWrapper);

        String billStatus = params.getOrDefault("bill_status", "").toString();
        String finishedTime = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);
        npRecordManager.updateRecord(result.getOrderNo(), billStatus, params.getOrDefault("amount_usdt", "0").toString(), finishedTime);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        if (StringUtils.isEmpty(billStatus)) {
            orderDetailVO.setStatus(2);
        } else if ("1".equals(billStatus)) {
            orderDetailVO.setStatus(3);
        } else if ("2".equals(billStatus)) {
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
        OrderNpRecordEntity record = npRecordManager.getByOrderNo(orderNo);

        Map<String, Object> params = new HashMap<>();
        params.put("in_order_id", record.getThirdOrderNo());
        params.put("user_id", record.getUserId());
        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/UtInRecordApi/confirmOrder", params, null);
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("NP 订单确认支付失败，params:{}, result:{}", params, resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public PaymentApplyResultVO paymentSwitch(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    @Override
    public void paymentCancel(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        OrderNpRecordEntity record = npRecordManager.getByOrderNo(orderNo);

        Map<String, Object> params = new HashMap<>();
        params.put("in_order_id", record.getThirdOrderNo());
        params.put("user_id", record.getUserId());
        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), "/UtInRecordApi/cancelOrder", params, null);
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        String status = resultObj.getString("status");
        if (!"success".equals(status)) {
            log.error("NP 订单取消失败，params:{}, result:{}", params, resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public PaymentApplyResultVO applyForWithdraw(PaymentChannelInfoVO paymentChannelInfoVO, OrderWithdrawForm form, String subOrderNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("serial_no", subOrderNo);
        data.put("amount", form.getAmount());
        data.put("bankcard_no", form.getBankAccount());
        data.put("user_name", form.getAccountHolder());
        data.put("bank_address", form.getBankBranch());

        Map<String, Object> params = new HashMap<>();
        params.put("sys_no", paymentChannelInfoVO.getMerAk());
        params.put("data", JSONObject.toJSONString(Collections.singletonList(data)));

        String sign = withdrawSign((String) params.get("data"), paymentChannelInfoVO.getMerAk(), paymentChannelInfoVO.getMerSk());
        params.put("sign", sign);

        NpExtVo extMap = JSONObject.parseObject(paymentChannelInfoVO.getExt(), NpExtVo.class);

        String resultStr = HttpUtil.apiPostForm(extMap.getWithdrawDomain(), "/AjaxOpen/saveOutOrder", params, null);
        log.info("NP cash request, mercode:{}, params:{}, result:{}", paymentChannelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("NP cash result is empty, mercode:{}, result:{}", paymentChannelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        Integer code = resultObj.getInteger("code");
        if (!code.equals(200)) {
            log.error("NP cash error, mercode:{}, result:{}, params:{}", paymentChannelInfoVO.getMerCode(), resultStr, JSONObject.toJSONString(params));
            npRecordManager.withdrawSave(paymentChannelInfoVO, params, resultObj);
            throw new BusinessException("cash fail");
        }
        OrderNpRecordEntity eypRecordEntity = npRecordManager.withdrawSave(paymentChannelInfoVO, params, resultObj);

        PaymentApplyResultVO resultVO = new PaymentApplyResultVO();
        resultVO.setThirdpartyOrderNo(eypRecordEntity.getOrderNo());
        return resultVO;
    }

    @Override
    public OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo) {
        return null;
    }

    @Override
    public OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params) {
        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, params.get("bill_no"));
        OrderNpRecordEntity result = npRecordManager.getOne(queryWrapper);

        String billStatus = params.getOrDefault("bill_status", "").toString();
        String finishedTime = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);
        npRecordManager.updateRecord(result.getOrderNo(), billStatus, "", finishedTime);

        OrderWithdrawDetailVO orderDetailVO = new OrderWithdrawDetailVO();
        if (StringUtils.isEmpty(billStatus)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.SUCCESSED);
        } else if ("1".equals(billStatus)) {
            orderDetailVO.setStatus(WithdrawSubOrderStatusEnum.FAILED);
        }
        orderDetailVO.setEndtime(finishedTime);
        return orderDetailVO;
    }

    public static String sign(String apiSecret, Object payload) {
        try {
            String queryString = joinQueryParams(payload);
            log.info("NP sign, queryString:{}", queryString + apiSecret);
            return DigestUtils.md5Hex(queryString + apiSecret);
        } catch (Exception e) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

    public static String callbackSign(String apiSecret, String content) {
        try {
            log.info("NP callback sign, queryString:{}", content + apiSecret);
            return DigestUtils.md5Hex(content + apiSecret);
        } catch (Exception e) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
    }

    public static String withdrawSign(String data, String ak, String sk) {
        try {
            return DigestUtils.md5Hex(data + ak + sk);
        } catch (Exception e) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
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

        return keyList.stream().map(v -> {
            try {
                return v + "=" + URLEncoder.encode(params.get(v).toString(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining("&"));
    }
}
