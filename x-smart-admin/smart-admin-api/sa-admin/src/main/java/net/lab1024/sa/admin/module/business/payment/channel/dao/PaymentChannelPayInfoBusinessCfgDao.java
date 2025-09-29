package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelPayInfoBusinessCfg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 支付渠道业务范围配置表 Dao
 */

@Mapper
@Component
public interface PaymentChannelPayInfoBusinessCfgDao extends BaseMapper<PaymentChannelPayInfoBusinessCfg> {


}
