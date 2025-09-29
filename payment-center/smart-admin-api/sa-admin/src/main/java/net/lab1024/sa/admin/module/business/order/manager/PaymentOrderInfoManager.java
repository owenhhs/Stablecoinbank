package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderInfoDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付订单信息表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Service
public class PaymentOrderInfoManager extends ServiceImpl<PaymentOrderInfoDao, PaymentOrderInfoEntity> {

    public PaymentOrderInfoEntity queryByOrderNo(String orderNo) {
        LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderInfoEntity::getOrderNo, orderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    public List<PaymentOrderInfoEntity> queryOrderListByChannelId(Long channelId) {
        LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderInfoEntity::getChannelId, channelId);
        queryWrapper.eq(PaymentOrderInfoEntity::getStatus, 1);
        queryWrapper.lt(PaymentOrderInfoEntity::getRetry, 5);
        queryWrapper.le(PaymentOrderInfoEntity::getExpiredTime, System.currentTimeMillis() - (5 * 60 * 1000));
        queryWrapper.last("limit 10");
        return baseMapper.selectList(queryWrapper);
    }

    public List<PaymentOrderInfoEntity> queryOrderListByHistory(Long channelId, Long businessId,
                                                                Long startTime, Long endTime) {
        LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderInfoEntity::getChannelId, channelId);
        queryWrapper.eq(PaymentOrderInfoEntity::getStatus, 2);
        queryWrapper.eq(PaymentOrderInfoEntity::getBusinessId, businessId);
        queryWrapper.between(PaymentOrderInfoEntity::getFinishedTime, startTime, endTime);
        return baseMapper.selectList(queryWrapper);
    }

    public List<DashboardOrderTodayVO> queryDashboardOrderTodayList() {
        return baseMapper.queryDashboardOrderToday();
    }


}
