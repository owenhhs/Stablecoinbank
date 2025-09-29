package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleCalcTaskEntity;
import net.lab1024.sa.admin.module.business.order.dao.SettleCalcTaskDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 结算单计算任务表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-10 14:51:11
 * @Copyright Sunny
 */
@Service
public class SettleCalcTaskManager extends ServiceImpl<SettleCalcTaskDao, SettleCalcTaskEntity> {


    public SettleCalcTaskEntity getTaskByTradeTime(String tradeTime) {
        LambdaQueryWrapper<SettleCalcTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleCalcTaskEntity::getTradeTime, tradeTime);
        return baseMapper.selectOne(queryWrapper);
    }

}
