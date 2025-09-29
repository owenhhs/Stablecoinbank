package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderFxmchRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderFxmchRecordEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 渠道管理平台请求记录  Manager
 *
 * @Author Sunny
 * @Date 2024-09-12 16:39:39
 * @Copyright Sunny
 */
@Service
public class OrderFxmchRecordManager extends ServiceImpl<OrderFxmchRecordDao, OrderFxmchRecordEntity> {


    public OrderFxmchRecordEntity save(PaymentChannelInfoVO channelInfoVO, OrderDepositForm depositForm, JSONObject resultObj) {

        OrderFxmchRecordEntity record = new OrderFxmchRecordEntity();
        record.setChannelId(channelInfoVO.getId());
        record.setOrderNo(depositForm.getOrderNo());
        record.setTradeType(PaymentTypeEnum.payment.getValue());
        record.setDepositType(depositForm.getDepositType());
        record.setPaymentChannel(depositForm.getPaymentChannel());
        int code = resultObj.getInteger("code");
        record.setCode(code);
        record.setMsg(resultObj.getString("msg"));
        if (code == 0) {
            JSONObject data = resultObj.getJSONObject("data");
            record.setPayurl(data.getString("payUrl"));
            record.setBankInfo(data.getString("bankInfo"));
            record.setBankName(data.getString("bankName"));
            record.setBankNo(data.getString("bankNo"));
            record.setUsername(data.getString("username"));
            record.setSubMerName(data.getString("merName"));
        }
        record.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<OrderFxmchRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFxmchRecordEntity::getOrderNo, depositForm.getOrderNo());
        long count = this.count(queryWrapper);
        if (count > 0) {
            this.update(record, queryWrapper);
        } else {
            record.setCreateTime(LocalDateTime.now());
            this.save(record);
        }
        return record;
    }

    public OrderFxmchRecordEntity updateRecord(String orderNo, JSONObject resultObj) {
        LambdaQueryWrapper<OrderFxmchRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFxmchRecordEntity::getOrderNo, orderNo);
        OrderFxmchRecordEntity record = this.getOne(queryWrapper);

        int code = resultObj.getInteger("code");
        record.setCode(code);
        record.setMsg(resultObj.getString("msg"));
        if (code == 0) {
            JSONObject data = resultObj.getJSONObject("data");
            record.setPayurl(data.getString("payUrl"));
            record.setBankInfo(data.getString("bankInfo"));
            record.setBankName(data.getString("bankName"));
            record.setBankNo(data.getString("bankNo"));
            record.setUsername(data.getString("username"));
            record.setSubMerName(data.getString("merName"));
        }
        record.setUpdateTime(LocalDateTime.now());
        this.updateById(record);
        return record;
    }


    public OrderFxmchRecordEntity getRecordByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderFxmchRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFxmchRecordEntity::getOrderNo, orderNo);
        return this.getOne(queryWrapper);
    }

    public void updateByOrderNo(String orderNo, Integer status) {
        LambdaQueryWrapper<OrderFxmchRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFxmchRecordEntity::getOrderNo, orderNo);
        OrderFxmchRecordEntity record = new OrderFxmchRecordEntity();
        record.setStatus(status);
        this.update(record, queryWrapper);
    }


}
