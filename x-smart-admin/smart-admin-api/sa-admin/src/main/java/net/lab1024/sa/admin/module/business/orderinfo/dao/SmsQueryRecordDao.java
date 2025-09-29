package net.lab1024.sa.admin.module.business.orderinfo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.SmsQueryRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 订单信息表 Dao
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Mapper
@Component
public interface SmsQueryRecordDao extends BaseMapper<SmsQueryRecordEntity> {


}
