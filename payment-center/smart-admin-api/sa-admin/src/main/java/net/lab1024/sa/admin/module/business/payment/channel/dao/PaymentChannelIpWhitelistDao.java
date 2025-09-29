package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelIpWhitelistEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道基本信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentChannelIpWhitelistDao extends BaseMapper<PaymentChannelIpWhitelistEntity> {


}
