package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleBillEntity;
import net.lab1024.sa.admin.module.business.order.dao.SettleBillDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 结算单表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Service
public class SettleBillManager extends ServiceImpl<SettleBillDao, SettleBillEntity> {

    public List<SettleBillEntity> getListByTradeTime(String tradeTime) {
        LambdaQueryWrapper<SettleBillEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleBillEntity::getTradeTime, tradeTime);
        queryWrapper.ne(SettleBillEntity::getSettleStatus, 2);
        return this.list(queryWrapper);
    }

    public boolean checkSettleCompleted(String tradeTime, Long channelId, Long businessId, String tradeType) {
        LambdaQueryWrapper<SettleBillEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleBillEntity::getSettleStatus, 3);
        queryWrapper.eq(SettleBillEntity::getTradeTime, tradeTime);
        queryWrapper.eq(SettleBillEntity::getChannelId, channelId);
        queryWrapper.eq(SettleBillEntity::getBusinessId, businessId);
        queryWrapper.eq(SettleBillEntity::getTradeType, tradeType);
        return this.count(queryWrapper) > 0;
    }

}
