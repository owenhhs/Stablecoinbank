package net.lab1024.sa.admin.module.openapi.order.dao;

import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderPayrockRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * payrack订单流水记录表 Dao
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */

@Mapper
@Component
public interface OrderPayrockRecordDao extends BaseMapper<OrderPayrockRecordEntity> {


}
