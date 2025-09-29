package net.lab1024.sa.admin.module.openapi.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.openapi.order.dao.PaymentOrderCallbackRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.PaymentOrderCallbackRecordEntity;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 订单状态回调记录表  Manager
 *
 * @Author bradydreamer
 * @Date 2025-01-09 14:35:52
 */
@Service
public class PaymentOrderCallbackRecordManager extends ServiceImpl<PaymentOrderCallbackRecordDao, PaymentOrderCallbackRecordEntity> {

    public Boolean hasSuccessCallbackRecord(String orderNo, Integer orderStatus) {
        LambdaQueryWrapper<PaymentOrderCallbackRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderCallbackRecordEntity::getOrderNo, orderNo);
        queryWrapper.eq(PaymentOrderCallbackRecordEntity::getOrderStatus, orderStatus);
        queryWrapper.eq(PaymentOrderCallbackRecordEntity::getCallbackStatus, 1);
        return count(queryWrapper) > 0;
    }

    public void saveCallbackRecord(String orderNo, Integer orderStatus, Integer status, String result) {
        PaymentOrderCallbackRecordEntity paymentOrderCallbackRecordEntity = new PaymentOrderCallbackRecordEntity();
        paymentOrderCallbackRecordEntity.setOrderNo(orderNo);
        paymentOrderCallbackRecordEntity.setOrderStatus(orderStatus);
        paymentOrderCallbackRecordEntity.setCallbackTime(SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS));
        paymentOrderCallbackRecordEntity.setCallbackStatus(status);
        paymentOrderCallbackRecordEntity.setCallbackResult(result);
        paymentOrderCallbackRecordEntity.setCreateTime(LocalDateTime.now());
        paymentOrderCallbackRecordEntity.setUpdateTime(LocalDateTime.now());
        save(paymentOrderCallbackRecordEntity);
    }


}
