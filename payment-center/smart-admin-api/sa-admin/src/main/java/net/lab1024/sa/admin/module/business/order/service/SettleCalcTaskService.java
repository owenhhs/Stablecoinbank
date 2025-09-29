package net.lab1024.sa.admin.module.business.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminRedisKeyConst;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleCalcTaskEntity;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.admin.module.business.order.manager.SettleCalcTaskManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.openapi.order.service.OrderPushService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.util.RedisLockUtil;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 结算单计算任务表 Service
 *
 * @Author Sunny
 * @Date 2024-09-10 14:51:11
 * @Copyright Sunny
 */
@Slf4j
@Component
public class SettleCalcTaskService {
    @Resource
    private SettleCalcTaskManager settleCalcTaskManager;
    @Resource
    private SettleBillService settleBillService;
    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;
    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private OrderPushService orderPushService;


    @Scheduled(cron = "0 0 2 * * *")
    public void paymentOrderSettleCalcTask() {
        String tradeTime = SmartLocalDateUtil.format(LocalDateTime.now().minusDays(1), SmartDateFormatterEnum.YMD);
        String redisKey = AdminRedisKeyConst.SETTLE_CALC_TASK_PAYMENT_LOCK + tradeTime;
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return;
        }
        try {
            SettleCalcTaskEntity taskEntity = settleCalcTaskManager.getTaskByTradeTime(tradeTime);
            if (taskEntity == null) {
                taskEntity = new SettleCalcTaskEntity();
                taskEntity.setTradeTime(tradeTime);
                taskEntity.setTaskStatus(0);
                taskEntity.setTradeType(PaymentTypeEnum.payment.getValue());
                taskEntity.setStartTime(System.currentTimeMillis());
                taskEntity.setCreateTime(LocalDateTime.now());
                taskEntity.setUpdateTime(LocalDateTime.now());
            }
            if (!taskEntity.getTaskStatus().equals(0)) {
                return;
            }
            taskEntity.setTaskStatus(1);
            taskEntity.setUpdateTime(LocalDateTime.now());
            settleCalcTaskManager.saveOrUpdate(taskEntity);

            settleBillService.paymentCalc(taskEntity);
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }


//    @Scheduled(initialDelay = 2000, fixedRate = 10000)
//    public void paymentOrderSettleCalcTaskRollBack() {
//        LambdaQueryWrapper<SettleCalcTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SettleCalcTaskEntity::getTaskStatus, 3);
//        SettleCalcTaskEntity taskEntity = settleCalcTaskManager.getOne(queryWrapper);
//        if (taskEntity == null) {
//            return;
//        }
//
//        String redisKey = AdminRedisKeyConst.SETTLE_CALC_TASK_PAYMENT_ROLLBACK_LOCK + taskEntity.getTradeTime();
//        boolean lock = RedisLockUtil.tryLock(redisKey);
//        if (!lock) {
//            return;
//        }
//        try {
//            if (!taskEntity.getTaskStatus().equals(3)) {
//                return;
//            }
//            taskEntity.setTaskStatus(1);
//            settleCalcTaskManager.updateById(taskEntity);
//
//            // 清除历史数据
//            List<SettleBillEntity> billEntities = settleBillManager.getListByTradeTime(taskEntity.getTradeTime());
//            for (SettleBillEntity billEntity : billEntities) {
//                settleOrderManager.deleteBySettleId(billEntity.getId());
//                settleBillManager.removeById(billEntity.getId());
//            }
//
//            // 重算
//            settleBillService.paymentCalc(taskEntity);
//        } finally {
//            RedisLockUtil.unlock(redisKey);
//        }
//
//    }


    @Scheduled(initialDelay = 2000, fixedRate = 10000)
    public void paymentOrderExpiredTask() {
        long currentTime = System.currentTimeMillis();
        LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderInfoEntity::getStatus, 1);
        queryWrapper.eq(PaymentOrderInfoEntity::getPaymentStatus, 1);
        queryWrapper.lt(PaymentOrderInfoEntity::getExpiredTime, currentTime);

        List<PaymentOrderInfoEntity> orderInfoEntityList = paymentOrderInfoManager.list(queryWrapper);

        for (PaymentOrderInfoEntity orderInfoEntity : orderInfoEntityList) {
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            orderInfoNew.setPaymentStatus(4);
            orderInfoNew.setStatus(3);
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);

            // 如果状态是过期，则不回调
            if (orderInfoEntity.getPaymentStatus().equals(4) || orderInfoEntity.getStatus().equals(4)) {
                continue;
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    log.info("paymentOrderExpiredTask orderNo:{}", orderInfoEntity.getOrderNo());
                    orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                            orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
                } catch (Exception e) {
                    log.error("paymentOrderExpiredTask orderNo:{}, error::{}, ", orderInfoEntity.getOrderNo(), e.getMessage(), e);
                }
            });
            executor.shutdown();
        }
    }

}
