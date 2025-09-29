package net.lab1024.sa.admin.module.openapi.order.dao;

import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderLetonggouRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * letonggou平台订单申请记录表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-05 17:14:49
 * @Copyright Sunny
 */

@Mapper
@Component
public interface OrderLetonggouRecordDao extends BaseMapper<OrderLetonggouRecordEntity> {


}
