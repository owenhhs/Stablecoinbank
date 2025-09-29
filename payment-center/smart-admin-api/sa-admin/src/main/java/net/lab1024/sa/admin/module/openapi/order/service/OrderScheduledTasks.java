package net.lab1024.sa.admin.module.openapi.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderDetailEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderDetailManager;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderInfoManager;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderDetailVO;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawDetailVO;
import net.lab1024.sa.base.common.enumeration.WithdrawOrderStatusEnum;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.util.RedisLockUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.constant.RedisKeyConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OrderScheduledTasks {

    @Resource
    private PaymentChannelInfoManager channelInfoManager;
    @Resource
    private PaymentOrderInfoManager orderInfoManager;
    @Resource
    private OrderPushService orderPushService;

    @Resource
    private CashOrderInfoManager cashOrderInfoManager;
    @Resource
    private CashOrderDetailManager cashOrderDetailManager;

    @Scheduled(initialDelay = 2000, fixedRate = 5 * 60000)
    public void paymentOrderStatusTask() {
        List<PaymentChannelInfoEntity> channelInfoEntityList = channelInfoManager.list();
//        PaymentChannelInfoEntity channelInfo = channelInfoEntityList.get(RandomUtils.nextInt(0, channelInfoEntityList.size()));
        for (PaymentChannelInfoEntity channelInfo : channelInfoEntityList) {
            List<String> platformList = Arrays.asList(PaymentPlatformEnum.ltg.getValue(), PaymentPlatformEnum.np.getValue());
            if (platformList.contains(channelInfo.getImplCode())) {
                return;
            }
            IPaymentPlatformService platformService = IPaymentPlatformService.get(channelInfo.getImplCode());

            // 获取订单状态
            String redisKey = RedisKeyConst.Order.PAYMENT_ORDER_STATUS_TASK_LOCK + channelInfo.getMerAk();
            boolean lock = RedisLockUtil.tryLock(redisKey);
            if (!lock) {
                return;
            }
            try {

                List<PaymentOrderInfoEntity> orderInfoEntityList = orderInfoManager.queryOrderListByChannelId(channelInfo.getId());

                for (PaymentOrderInfoEntity orderInfoEntity : orderInfoEntityList) {
                    log.info("paymentOrderStatusTask orderNo:{}", orderInfoEntity.getOrderNo());
                    PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
                    orderInfoNew.setId(orderInfoEntity.getId());
                    // 查询订单信息
                    OrderDetailVO orderDetailVO = platformService.getPaymentOrderDetail(channelInfo, orderInfoEntity.getOrderNo());
                    if (orderDetailVO == null) {
                        continue;
                    }
                    if (orderDetailVO.getStatus().equals(1)) {
                        orderInfoNew.setRetry(orderInfoEntity.getRetry() + 1);
                        orderInfoNew.setUpdateTime(LocalDateTime.now());
                        orderInfoManager.updateById(orderInfoNew);
                        continue;
                    } else if (orderDetailVO.getStatus().equals(4)) {
                        continue;
                    }

                    // 更新数据
                    if (StringUtils.isNotEmpty(orderDetailVO.getEndtime())) {
                        try {
                            long endTime = SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime());
                            if (endTime > 0) {
                                orderInfoNew.setFinishedTime(endTime);
                            }
                        } catch (Exception e) {
                            log.error("payment task orderPush channelId:{}, orderNo:{}, exception", channelInfo.getId(), orderInfoEntity.getOrderNo(), e);
                        }
                    }

                    if (orderDetailVO.getStatus().equals(2)) {
                        orderInfoNew.setPaymentStatus(2);
                    }
                    orderInfoNew.setStatus(orderDetailVO.getStatus());
                    orderInfoNew.setUpdateTime(LocalDateTime.now());
                    orderInfoManager.updateById(orderInfoNew);
                    // 发起通知
                    try {
                        log.info("paymentOrderStatusTask orderNo:{}, status:{}", orderInfoEntity.getOrderNo(), orderInfoNew.getStatus());
                        orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                                orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
                    } catch (Exception e) {
                        log.error("payment task orderPush channelId:{}, orderNo:{}, exception", channelInfo.getId(), orderInfoEntity.getOrderNo(), e);
                    }
                }
            } finally {
                RedisLockUtil.unlock(redisKey);
            }
        }
    }

    /**
     * 定时任务：确认子订单状态和主订单状态
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5 * 60000)
    public void withdrawOrderStatusTask() {

        // 获取订单状态
        String redisKey = RedisKeyConst.Order.WITHDRAW_ORDER_STATUS_TASK_LOCK + "timing-comfirm";
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return;
        }
        try {
            //先对子订单确认交易状态
            LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CashOrderDetailEntity::getStatus, WithdrawSubOrderStatusEnum.PROCESSING.getValue());
            List<CashOrderDetailEntity> orderDetailList = cashOrderDetailManager.list(queryWrapper);
            for (CashOrderDetailEntity orderDetailEntity : orderDetailList) {
                // 查询订单信息
                PaymentChannelInfoEntity channelInfo = channelInfoManager.getById(orderDetailEntity.getChannelId());
                IPaymentPlatformService platformService = IPaymentPlatformService.get(channelInfo.getImplCode());

                OrderWithdrawDetailVO orderDetailVO = platformService.getWithdrawOrderDetail(channelInfo, orderDetailEntity.getOrderNo());
                if (WithdrawSubOrderStatusEnum.PROCESSING.equals(orderDetailVO.getStatus())) {
                    orderDetailEntity.setRetry(orderDetailEntity.getRetry() + 1);
                    orderDetailEntity.setUpdateTime(LocalDateTime.now());
                    cashOrderDetailManager.updateById(orderDetailEntity);
                    continue;
                }
                // 更新数据
                if (StringUtils.isNotEmpty(orderDetailVO.getEndtime())) {
                    try {
                        long endTime = SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime());
                        if (endTime > 0) {
                            orderDetailEntity.setFinishedTime(endTime);
                        }
                    } catch (Exception e) {
                        log.warn("format endtime error, {}", e.getMessage());
                    }
                }
                orderDetailEntity.setStatus(orderDetailVO.getStatus().getValue());
                orderDetailEntity.setUpdateTime(LocalDateTime.now());
                cashOrderDetailManager.updateById(orderDetailEntity);
                // TODO 判断主订单下的子订单是否都已状态确定，如果是，则更改主订单状态（如果已经成功的）
//                try {
//                    log.info("paymentOrderStatusTask orderNo:{}", orderDetailEntity.getOrderNo());
//                    orderPushService.paymentOrderPush(orderDetailEntity);
//                } catch (Exception e) {
//                    log.error("payment task orderPush channelId:{}, orderNo:{}, exception", channelInfo.getId(), orderDetailEntity.getOrderNo(), e);
//                }
            }

            //对主订单进行状态确认
            LambdaQueryWrapper<CashOrderInfoEntity> orderQueryWrapper = new LambdaQueryWrapper<>();
            orderQueryWrapper.eq(CashOrderInfoEntity::getStatus, WithdrawOrderStatusEnum.Processing);
            List<CashOrderInfoEntity> orderList = cashOrderInfoManager.list(orderQueryWrapper);
            for (CashOrderInfoEntity orderEntity : orderList) {
                // 查询订单明细信息
                long countSubOrdersInProgress = cashOrderDetailManager.countSubOrdersInProgress(orderEntity.getOrderNo());
                if (countSubOrdersInProgress > 0) {
                    //还有未确定状态的订单，跳过
                    continue;
                }
                // 计算成功子订单的总金额
                BigDecimal amountSuccessed = cashOrderDetailManager.sumSubOrdersSuccessed(orderEntity.getOrderNo());
                if (amountSuccessed.compareTo(BigDecimal.ZERO) < 0) {
                    log.error("查询出金订单(orderNo:{})已经成功的子订单的总金额失败！！", orderEntity.getOrderNo());
                    continue;
                }
                if (amountSuccessed.compareTo(orderEntity.getAmount()) == 0) {
                    //相等
                    orderEntity.setFinishedTime(SmartLocalDateUtil.getTimestamp(LocalDateTime.now()));
                    orderEntity.setStatus(WithdrawOrderStatusEnum.Successed.getValue());

                } else {
                    orderEntity.setStatus(WithdrawOrderStatusEnum.Pending.getValue());
                    orderEntity.setManualFlag(CashOrderInfoEntity.MANUAL_FLAG_YES);
                    orderEntity.setManualReason("已成功金额与总金额不一致;" + orderEntity.getManualReason());
                }
                orderEntity.setUpdateTime(LocalDateTime.now());
                cashOrderInfoManager.updateById(orderEntity);
            }

        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

}
