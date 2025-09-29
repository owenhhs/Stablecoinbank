package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLetonggouRecordEntity;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderLetonggouRecordDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * letonggou平台订单申请记录表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-05 17:14:49
 * @Copyright Sunny
 */
@Service
public class OrderLetonggouRecordManager extends ServiceImpl<OrderLetonggouRecordDao, OrderLetonggouRecordEntity> {

    public OrderLetonggouRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderLetonggouRecordEntity orderSudingRecordEntity = new OrderLetonggouRecordEntity();
        orderSudingRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderSudingRecordEntity.setOrderNo((String) params.get("out_trade_no"));
        orderSudingRecordEntity.setTradeType(PaymentTypeEnum.payment.getValue());
        orderSudingRecordEntity.setPaymentType((String) params.get("type"));

        int code = resultObj.getInteger("code");
        orderSudingRecordEntity.setCode(code);
        orderSudingRecordEntity.setMsg(resultObj.getString("msg"));
        if (code == 1) {
            orderSudingRecordEntity.setTradeNo(resultObj.getString("trade_no"));
            orderSudingRecordEntity.setPayurl(resultObj.getString("payurl"));
            orderSudingRecordEntity.setQrcode(resultObj.getString("qrcode"));
            orderSudingRecordEntity.setUrlscheme(resultObj.getString("urlscheme"));
        }
        orderSudingRecordEntity.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderLetonggouRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLetonggouRecordEntity::getOrderNo, params.get("out_trade_no"));
        if (this.count(queryWrapper) > 0) {
            this.update(orderSudingRecordEntity, queryWrapper);
        } else {
            orderSudingRecordEntity.setCreateTime(LocalDateTime.now());
            this.save(orderSudingRecordEntity);
        }
        return orderSudingRecordEntity;
    }

    public void updateRecord(String orderNo, String apiTradeNo, Integer status, String buyer, String addtime, String endtime) {

        LambdaQueryWrapper<OrderLetonggouRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLetonggouRecordEntity::getOrderNo, orderNo);
        OrderLetonggouRecordEntity orderSudingRecordEntity = new OrderLetonggouRecordEntity();
        orderSudingRecordEntity.setApiTradeNo(apiTradeNo);
        orderSudingRecordEntity.setStatus(status);
        orderSudingRecordEntity.setBuyer(buyer);
        orderSudingRecordEntity.setAddtime(addtime);
        orderSudingRecordEntity.setEndtime(endtime);
        orderSudingRecordEntity.setUpdateTime(LocalDateTime.now());
        this.update(orderSudingRecordEntity, queryWrapper);
    }

}
