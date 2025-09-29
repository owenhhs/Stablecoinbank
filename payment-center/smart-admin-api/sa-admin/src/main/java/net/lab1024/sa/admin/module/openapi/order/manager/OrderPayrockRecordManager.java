package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLetonggouRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderPayrockRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderPayrockRecordDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
public class OrderPayrockRecordManager extends ServiceImpl<OrderPayrockRecordDao, OrderPayrockRecordEntity> {

    public OrderPayrockRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderPayrockRecordEntity orderPayrockRecordEntity = new OrderPayrockRecordEntity();
        orderPayrockRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderPayrockRecordEntity.setOrderNo((String) params.get("cartId"));
        orderPayrockRecordEntity.setTradeType(PaymentTypeEnum.payment.getValue());
        orderPayrockRecordEntity.setPaymentMethod((String) params.get("paymentMethod"));
        orderPayrockRecordEntity.setAmount((BigDecimal) params.get("amount"));
        orderPayrockRecordEntity.setCurrency((String) params.get("currency"));
        orderPayrockRecordEntity.setCountry((String) params.get("country"));
        orderPayrockRecordEntity.setEmail((String) params.get("email"));

        String status = resultObj.getString("status");
        orderPayrockRecordEntity.setStatus(status);
        if (status.equals("IN_PROGRESS")) {
            orderPayrockRecordEntity.setReference(resultObj.getString("reference"));
            orderPayrockRecordEntity.setPaymentUrl(resultObj.getString("paymentUrl"));
        }
        orderPayrockRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderPayrockRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPayrockRecordEntity::getOrderNo, params.get("cartId"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderPayrockRecordEntity, queryWrapper);
        } else {
            orderPayrockRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderPayrockRecordEntity);
        }
        return orderPayrockRecordEntity;
    }

    public void updateRecord(String orderNo, String status, String message, String amount, String orderTime, String completedTime) {
        LambdaQueryWrapper<OrderPayrockRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPayrockRecordEntity::getOrderNo, orderNo);
        OrderPayrockRecordEntity orderPayrockRecordEntity = new OrderPayrockRecordEntity();
        orderPayrockRecordEntity.setStatus(status);
        orderPayrockRecordEntity.setStatusMessage(message);
        orderPayrockRecordEntity.setOrderAmount(new BigDecimal(amount));
        orderPayrockRecordEntity.setOrderTime(orderTime);
        orderPayrockRecordEntity.setCompletedTime(completedTime);
        orderPayrockRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderPayrockRecordEntity, queryWrapper);
    }

}
