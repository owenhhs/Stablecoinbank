package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderDetailDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderDetailEntity;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 兑付订单拆分明细表  Manager
 */
@Service
public class CashOrderDetailManager extends ServiceImpl<CashOrderDetailDao, CashOrderDetailEntity> {

    public CashOrderDetailEntity queryBySubOrderNo(String subOrderNo) {
        LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderDetailEntity::getSubOrderNo, subOrderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    public List<CashOrderDetailEntity> querySuccessedSubOrdersByOrderNo(String orderNo) {
        LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderDetailEntity::getOrderNo, orderNo);
        queryWrapper.eq(CashOrderDetailEntity::getStatus, WithdrawSubOrderStatusEnum.SUCCESSED.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    public List<CashOrderDetailEntity> queryProcessingSubOrdersByOrderNo(String orderNo) {
        LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderDetailEntity::getOrderNo, orderNo);
        queryWrapper.eq(CashOrderDetailEntity::getStatus, WithdrawSubOrderStatusEnum.PROCESSING.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    public List<CashOrderDetailEntity> queryFailedSubOrdersByOrderNo(String orderNo) {
        LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderDetailEntity::getOrderNo, orderNo);
        queryWrapper.eq(CashOrderDetailEntity::getStatus, WithdrawSubOrderStatusEnum.FAILED.getValue());
        return baseMapper.selectList(queryWrapper);
    }

    public long countSubOrdersInProgress(String orderNo) {
        LambdaQueryWrapper<CashOrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CashOrderDetailEntity::getOrderNo, orderNo);
        queryWrapper.eq(CashOrderDetailEntity::getStatus, WithdrawSubOrderStatusEnum.PROCESSING.getValue());
        return baseMapper.selectCount(queryWrapper);
    }

    public BigDecimal sumSubOrdersInProgress(String orderNo) {
        QueryWrapper<CashOrderDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(amount) as amount");
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.eq("status", WithdrawSubOrderStatusEnum.PROCESSING.getValue());
        CashOrderDetailEntity sumEntity = baseMapper.selectOne(queryWrapper);
        if (sumEntity == null) {
            return BigDecimal.ZERO;
        }
        return sumEntity.getAmount();
    }

    public BigDecimal sumSubOrdersSuccessed(String orderNo) {
        QueryWrapper<CashOrderDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(amount) as amount");
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.eq("status", WithdrawSubOrderStatusEnum.SUCCESSED.getValue());
        CashOrderDetailEntity sumEntity = baseMapper.selectOne(queryWrapper);
        if (sumEntity == null) {
            return BigDecimal.ZERO;
        }
        return sumEntity.getAmount();
    }

    public List<DashboardOrderTodayVO> queryDashboardOrderTodayList() {
        return baseMapper.queryDashboardOrderToday();
    }
}
