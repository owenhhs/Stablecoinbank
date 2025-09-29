package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderUsepayRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderUsepayRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * usepay订单流水记录表  Manager
 *
 * @Author bradydreamer
 * @Date 2025-01-09 14:35:52
 */
@Service
public class OrderUsepayRecordManager extends ServiceImpl<OrderUsepayRecordDao, OrderUsepayRecordEntity> {

    public OrderUsepayRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj, PaymentTypeEnum paymentTypeEnum) {
        OrderUsepayRecordEntity orderUsepayRecordEntity = new OrderUsepayRecordEntity();
        orderUsepayRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderUsepayRecordEntity.setOrderNo((String) params.get("requestid"));
        orderUsepayRecordEntity.setTradeType(paymentTypeEnum.getValue());
//        orderUsepayRecordEntity.setPaymentMethod((String) params.get("paymentMethod"));
        orderUsepayRecordEntity.setAmount((BigDecimal) params.get("amount"));
//        orderUsepayRecordEntity.setCurrency((String) params.get("currency"));
//        orderUsepayRecordEntity.setCountry((String) params.get("country"));
//        orderUsepayRecordEntity.setEmail((String) params.get("email"));

        String status = resultObj.getString("status");
        orderUsepayRecordEntity.setStatus(status);
        if (status.equals("IN_PROGRESS")) {
            orderUsepayRecordEntity.setReference(resultObj.getString("reference"));
            orderUsepayRecordEntity.setPaymentUrl(resultObj.getString("paymentUrl"));
        }
        orderUsepayRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderUsepayRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUsepayRecordEntity::getOrderNo, params.get("cartId"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderUsepayRecordEntity, queryWrapper);
        } else {
            orderUsepayRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderUsepayRecordEntity);
        }
        return orderUsepayRecordEntity;
    }

    public void updateRecord(String orderNo, Map<String, Object> dataJson) {
        LambdaQueryWrapper<OrderUsepayRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderUsepayRecordEntity::getOrderNo, orderNo);
        OrderUsepayRecordEntity orderUsepayRecordEntity = new OrderUsepayRecordEntity();
        orderUsepayRecordEntity.setStatus((String) dataJson.get("paystatus"));
        orderUsepayRecordEntity.setStatusMessage((String) dataJson.get("paystatusdesc"));
        orderUsepayRecordEntity.setOrderAmount(new BigDecimal((String) dataJson.get("amount")));
        orderUsepayRecordEntity.setCompletedTime((String) dataJson.get("paytime"));
        orderUsepayRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderUsepayRecordEntity, queryWrapper);
    }

}
