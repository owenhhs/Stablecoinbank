package net.lab1024.sa.admin.module.openapi.order.dao;

import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderFxmchRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 渠道管理平台请求记录 Dao
 *
 * @Author Sunny
 * @Date 2024-09-12 16:39:39
 * @Copyright Sunny
 */

@Mapper
@Component
public interface OrderFxmchRecordDao extends BaseMapper<OrderFxmchRecordEntity> {


}
