package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderLypRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLypRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * lyp订单流水记录表  Manager
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */
@Service
public class OrderLypRecordManager extends ServiceImpl<OrderLypRecordDao, OrderLypRecordEntity> {

    public OrderLypRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj, PaymentTypeEnum paymentType) {
        OrderLypRecordEntity orderLypRecordEntity = new OrderLypRecordEntity();
        orderLypRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderLypRecordEntity.setOrderNo((String) params.get("orderNo"));
        orderLypRecordEntity.setTradeType(paymentType.getValue());

        orderLypRecordEntity.setDepositType((String) params.get("depositType"));
        orderLypRecordEntity.setPaymentChannel((String)params.get("paymentChannel"));
        orderLypRecordEntity.setAmount((BigDecimal) params.get("amount"));

        Integer code = resultObj.getInteger("code");
        orderLypRecordEntity.setCode(code);
        orderLypRecordEntity.setMsg(resultObj.getString("msg"));
        if (code.equals(200)) {
            JSONObject data = (JSONObject) resultObj.get("data");
            if (PaymentTypeEnum.payment.equals(paymentType)) {
                orderLypRecordEntity.setOrderInfoLink(data.getString("orderInfoLink"));

                JSONArray accountList = (JSONArray) data.get("accountList");
                JSONObject account = (JSONObject) accountList.get(0);
                orderLypRecordEntity.setQrCodeContent(account.getString("qrCodeContent"));

                orderLypRecordEntity.setAccount(resultObj.getString("account"));
                orderLypRecordEntity.setAccountName(resultObj.getString("name"));
                orderLypRecordEntity.setBankName(resultObj.getString("bank"));
                orderLypRecordEntity.setBankBranch(resultObj.getString("branch"));
                orderLypRecordEntity.setBankCode(resultObj.getString("bankCode"));
            }
        }
        orderLypRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderLypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLypRecordEntity::getOrderNo, params.get("orderNo"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderLypRecordEntity, queryWrapper);
        } else {
            orderLypRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderLypRecordEntity);
        }
        return orderLypRecordEntity;
    }

    public void updateRecord(String orderNo, Integer status, String result) {
        LambdaQueryWrapper<OrderLypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLypRecordEntity::getOrderNo, orderNo);
        OrderLypRecordEntity orderLypRecordEntity = new OrderLypRecordEntity();
        orderLypRecordEntity.setStatus(status);
        orderLypRecordEntity.setResult(result);
        orderLypRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderLypRecordEntity, queryWrapper);
    }

}
