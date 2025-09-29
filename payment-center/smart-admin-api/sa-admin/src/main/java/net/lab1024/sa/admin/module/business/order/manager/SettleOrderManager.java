package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.order.dao.SettleOrderDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleOrderEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 结算单订单关联表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */
@Service
public class SettleOrderManager extends ServiceImpl<SettleOrderDao, SettleOrderEntity> {

    public void deleteBySettleId(Long settleId) {
        LambdaQueryWrapper<SettleOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SettleOrderEntity::getSettleBillId, settleId);
        this.remove(queryWrapper);
    }

}
