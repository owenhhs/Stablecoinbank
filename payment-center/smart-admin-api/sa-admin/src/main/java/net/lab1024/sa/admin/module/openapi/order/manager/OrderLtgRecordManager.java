package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderLtgRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLtgRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * payrack订单流水记录表  Manager
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */
@Service
public class OrderLtgRecordManager extends ServiceImpl<OrderLtgRecordDao, OrderLtgRecordEntity> {

    public OrderLtgRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderLtgRecordEntity orderLtgRecordEntity = new OrderLtgRecordEntity();
        orderLtgRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderLtgRecordEntity.setOrderNo((String) params.get("mchOrderNo"));
        orderLtgRecordEntity.setTradeType(PaymentTypeEnum.payment.getValue());
        orderLtgRecordEntity.setPaymentMethod((String) params.get("wayCode"));
        orderLtgRecordEntity.setAmount((Long) params.get("amount"));
        orderLtgRecordEntity.setCurrency((String) params.get("currency"));
        orderLtgRecordEntity.setCountry((String) params.get("country"));
        orderLtgRecordEntity.setOrderTime(String.valueOf(params.get("reqTime")));

        int code = resultObj.getInteger("code");
        if (code == 0) {
            JSONObject data = resultObj.getJSONObject("data").getJSONObject("originalResponse");

            int orderState = data.getInteger("orderState");
            orderLtgRecordEntity.setOrderState(String.valueOf(orderState));
            if (orderState == 1) {
                orderLtgRecordEntity.setPayDataType(data.getString("payDataType"));
                orderLtgRecordEntity.setPayData(data.getString("payData"));
                orderLtgRecordEntity.setPayOrderId(data.getString("payOrderId"));
            }
        }
        orderLtgRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderLtgRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLtgRecordEntity::getOrderNo, params.get("mchOrderNo"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderLtgRecordEntity, queryWrapper);
        } else {
            orderLtgRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderLtgRecordEntity);
        }
        return orderLtgRecordEntity;
    }

    public void updateRecord(String orderNo, String status, long amount, String orderTime, String completedTime, String appId) {
        LambdaQueryWrapper<OrderLtgRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLtgRecordEntity::getOrderNo, orderNo);
        OrderLtgRecordEntity orderLtgRecordEntity = new OrderLtgRecordEntity();
        orderLtgRecordEntity.setAppId(appId);
        orderLtgRecordEntity.setOrderState(status);
        orderLtgRecordEntity.setOrderAmount(amount);
        orderLtgRecordEntity.setOrderTime(orderTime);
        orderLtgRecordEntity.setCompletedTime(completedTime);
        orderLtgRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderLtgRecordEntity, queryWrapper);
    }

}
