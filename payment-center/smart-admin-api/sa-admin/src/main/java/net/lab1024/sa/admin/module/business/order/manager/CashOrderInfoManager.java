package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderInfoDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import org.springframework.stereotype.Service;

/**
 * 兑付订单信息表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Service
public class CashOrderInfoManager extends ServiceImpl<CashOrderInfoDao, CashOrderInfoEntity> {

    public CashOrderInfoEntity queryByOrderNo(String orderNo) {
        LambdaQueryWrapper<CashOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderInfoEntity::getOrderNo, orderNo);
        return baseMapper.selectOne(queryWrapper);
    }
}
