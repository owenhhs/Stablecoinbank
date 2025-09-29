package net.lab1024.sa.admin.module.business.order.dao;

import net.lab1024.sa.admin.module.business.order.domain.entity.SettleCalcTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 结算单计算任务表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-10 14:51:11
 * @Copyright Sunny
 */

@Mapper
@Component
public interface SettleCalcTaskDao extends BaseMapper<SettleCalcTaskEntity> {

}
