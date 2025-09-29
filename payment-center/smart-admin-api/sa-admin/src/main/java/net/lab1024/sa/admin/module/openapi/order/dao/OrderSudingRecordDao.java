package net.lab1024.sa.admin.module.openapi.order.dao;

import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderSudingRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 速鼎订单流水 Dao
 *
 * @Author Sunny
 * @Date 2024-09-04 15:18:34
 * @Copyright Sunny
 */

@Mapper
@Component
public interface OrderSudingRecordDao extends BaseMapper<OrderSudingRecordEntity> {

}
