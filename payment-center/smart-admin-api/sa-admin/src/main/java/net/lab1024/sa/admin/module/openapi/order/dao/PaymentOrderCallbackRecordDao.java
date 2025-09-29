package net.lab1024.sa.admin.module.openapi.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.PaymentOrderCallbackRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 订单状态回调记录表 Dao
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentOrderCallbackRecordDao extends BaseMapper<PaymentOrderCallbackRecordEntity> {


}
