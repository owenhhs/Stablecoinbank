package net.lab1024.sa.admin.module.openapi.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderUsepayRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * usepay订单流水记录表 Dao
 *
 * @Author bradydreamer
 * @Date 2025-01-09 14:35:52
 */

@Mapper
@Component
public interface OrderUsepayRecordDao extends BaseMapper<OrderUsepayRecordEntity> {


}
