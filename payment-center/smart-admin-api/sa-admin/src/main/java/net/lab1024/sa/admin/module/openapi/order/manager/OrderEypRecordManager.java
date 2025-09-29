package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderEypRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderEypRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * eyp订单流水记录表  Manager
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */
@Service
public class OrderEypRecordManager extends ServiceImpl<OrderEypRecordDao, OrderEypRecordEntity> {

    public OrderEypRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj, PaymentTypeEnum paymentType) {
        OrderEypRecordEntity orderPayrockRecordEntity = new OrderEypRecordEntity();
        orderPayrockRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderPayrockRecordEntity.setOrderNo((String) params.get("merchant_order_no"));
        orderPayrockRecordEntity.setTradeType(paymentType.getValue());
        orderPayrockRecordEntity.setPaymentMethod((String) params.get("channel_type"));
        orderPayrockRecordEntity.setAmount((BigDecimal) params.get("amount"));
        orderPayrockRecordEntity.setCurrency((String) params.get("currency"));
        orderPayrockRecordEntity.setCountry((String) params.get("country"));
        orderPayrockRecordEntity.setEmail((String) params.get("email"));

        if (PaymentTypeEnum.payment.equals(paymentType)) {
            String status = resultObj.getString("status");
            orderPayrockRecordEntity.setStatus(status);
            orderPayrockRecordEntity.setStatusMessage(resultObj.getString("msg"));
            if ("success".equals(status)) {
                JSONObject data = (JSONObject) resultObj.get("data");
                orderPayrockRecordEntity.setPlatformOrderNo(data.getString("platform_order_no"));
                orderPayrockRecordEntity.setRealAmount(data.getBigDecimal("real_amount"));
                orderPayrockRecordEntity.setCreatedAt(data.getString("created_at"));
                orderPayrockRecordEntity.setExpiredAt(data.getString("expired_at"));

                orderPayrockRecordEntity.setBankCard(resultObj.getString("bank_card"));
                orderPayrockRecordEntity.setBankName(resultObj.getString("bank_name"));
                orderPayrockRecordEntity.setBankBranch(resultObj.getString("bank_branch"));

            }
        } else if (resultObj.getBoolean("success")) {
            String platformOrderNo = resultObj.getString("platform_order_no");
            orderPayrockRecordEntity.setPlatformOrderNo(platformOrderNo);
        }
        orderPayrockRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderEypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEypRecordEntity::getOrderNo, params.get("merchant_order_no"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderPayrockRecordEntity, queryWrapper);
        } else {
            orderPayrockRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderPayrockRecordEntity);
        }
        return orderPayrockRecordEntity;
    }

    public void updateRecord(String orderNo, String status, String amount, String completedTime) {
        LambdaQueryWrapper<OrderEypRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEypRecordEntity::getOrderNo, orderNo);
        OrderEypRecordEntity orderPayrockRecordEntity = new OrderEypRecordEntity();
        orderPayrockRecordEntity.setStatus(status);
        orderPayrockRecordEntity.setStatusMessage(status);
        orderPayrockRecordEntity.setRealAmount(new BigDecimal(amount));
        orderPayrockRecordEntity.setSucceededAt(completedTime);
        orderPayrockRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderPayrockRecordEntity, queryWrapper);
    }

}
