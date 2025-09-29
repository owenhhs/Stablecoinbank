package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderExlRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderExlRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
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
public class OrderExlRecordManager extends ServiceImpl<OrderExlRecordDao, OrderExlRecordEntity> {

    public OrderExlRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj, PaymentTypeEnum paymentType) {
        OrderExlRecordEntity orderExlRecordEntity = new OrderExlRecordEntity();
        orderExlRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderExlRecordEntity.setTradeType(paymentType.getValue());
        orderExlRecordEntity.setOrderNo((String) params.get("orderId"));
        orderExlRecordEntity.setAmount((BigDecimal) params.get("money"));
        orderExlRecordEntity.setPayerName((String) params.get("payerName"));
        if (params.get("uniqueCode") != null) {
            orderExlRecordEntity.setUniqueCode((String) params.get("uniqueCode"));
        }
        Integer code = resultObj.getInteger("code");
        orderExlRecordEntity.setCode(code);
        orderExlRecordEntity.setMsg(resultObj.getString("message"));
        if (code.equals(1)) {
            String data = (String) resultObj.get("data");
            orderExlRecordEntity.setPayUrl(data);
        }
        orderExlRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderExlRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderExlRecordEntity::getOrderNo, params.get("orderId"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderExlRecordEntity, queryWrapper);
        } else {
            orderExlRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderExlRecordEntity);
        }
        return orderExlRecordEntity;
    }

    public void updateRecord(String orderNo, String tradeStatus) {
        LambdaQueryWrapper<OrderExlRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderExlRecordEntity::getOrderNo, orderNo);
        OrderExlRecordEntity orderExlRecordEntity = new OrderExlRecordEntity();
        orderExlRecordEntity.setTradeStatus(tradeStatus);
        orderExlRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderExlRecordEntity, queryWrapper);
    }

}
