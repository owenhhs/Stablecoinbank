package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelParentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 渠道基本信息表 Dao
 */

@Mapper
@Component
public interface PaymentChannelParentDao extends BaseMapper<PaymentChannelParentEntity> {

}
