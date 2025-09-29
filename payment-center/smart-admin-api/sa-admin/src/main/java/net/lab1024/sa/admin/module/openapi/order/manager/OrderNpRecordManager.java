package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderNpRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderNpRecordEntity;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.apache.commons.lang3.StringUtils;
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
public class OrderNpRecordManager extends ServiceImpl<OrderNpRecordDao, OrderNpRecordEntity> {

    public OrderNpRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderNpRecordEntity orderNpRecordEntity = new OrderNpRecordEntity();
        orderNpRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderNpRecordEntity.setTradeType(PaymentTypeEnum.payment.getValue());
        orderNpRecordEntity.setOrderNo((String) params.get("order_id"));
        orderNpRecordEntity.setAmount((BigDecimal) params.get("order_amount"));
        orderNpRecordEntity.setOrderIp((String) params.get("order_ip"));
        orderNpRecordEntity.setOrderTime((String) params.get("order_time"));
        orderNpRecordEntity.setPayUserName((String) params.get("pay_user_name"));

        String status = resultObj.getString("status");
        if ("success".equals(status)) {
            JSONObject data = (JSONObject) resultObj.get("data");
            orderNpRecordEntity.setSendUrl(data.getString("send_url"));
            orderNpRecordEntity.setUserId(data.getString("user_id"));
            orderNpRecordEntity.setThirdOrderNo(data.getString("order_no"));
        }

        orderNpRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, params.get("order_id"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderNpRecordEntity, queryWrapper);
        } else {
            orderNpRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderNpRecordEntity);
        }
        return orderNpRecordEntity;
    }


    public OrderNpRecordEntity withdrawSave(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderNpRecordEntity orderNpRecordEntity = new OrderNpRecordEntity();
        orderNpRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderNpRecordEntity.setTradeType(PaymentTypeEnum.cash.getValue());
        orderNpRecordEntity.setOrderNo((String) params.get("serial_no"));
        orderNpRecordEntity.setAmount((BigDecimal) params.get("amount"));
        orderNpRecordEntity.setPayUserName((String) params.get("user_name"));

        orderNpRecordEntity.setWithdrawData(resultObj.toJSONString());
        orderNpRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, params.get("serial_no"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderNpRecordEntity, queryWrapper);
        } else {
            orderNpRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderNpRecordEntity);
        }
        return orderNpRecordEntity;
    }

    public void updateRecord(String orderNo, String status, String amountUsdt, String completedTime) {
        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, orderNo);
        OrderNpRecordEntity orderPayrockRecordEntity = new OrderNpRecordEntity();
        orderPayrockRecordEntity.setBillStatus(StringUtils.isEmpty(status) ? "0" : status);
//        orderPayrockRecordEntity.setAmountUsdt(new BigDecimal(amount));
        orderPayrockRecordEntity.setAmountUsdt(amountUsdt);
        orderPayrockRecordEntity.setFinishedTime(completedTime);
        orderPayrockRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderPayrockRecordEntity, queryWrapper);
    }

    public OrderNpRecordEntity getByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderNpRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderNpRecordEntity::getOrderNo, orderNo);
        return this.getOne(queryWrapper);
    }

}
