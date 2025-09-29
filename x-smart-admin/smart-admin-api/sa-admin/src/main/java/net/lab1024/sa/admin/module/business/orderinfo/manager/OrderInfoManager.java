package net.lab1024.sa.admin.module.business.orderinfo.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.lab1024.sa.admin.module.business.orderinfo.dao.OrderInfoDao;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 订单信息表  Manager
 */
@Service
public class OrderInfoManager extends ServiceImpl<OrderInfoDao, OrderInfoEntity> {

    public OrderInfoEntity queryByOrderNo(String orderNo) {
        QueryWrapper<OrderInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        //queryWrapper.eq("department_id", departmentId);
        return baseMapper.selectOne(queryWrapper);
    }

}
