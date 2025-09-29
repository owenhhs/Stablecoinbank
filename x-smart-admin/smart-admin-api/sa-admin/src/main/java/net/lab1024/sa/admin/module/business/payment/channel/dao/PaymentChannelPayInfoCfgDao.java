package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelPayInfoCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelPayInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelPayInfoCfgVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付渠道配置支付具体信息配置表 Dao
 */

@Mapper
@Component
public interface PaymentChannelPayInfoCfgDao extends BaseMapper<PaymentChannelPayInfoCfgEntity> {

    List<PaymentChannelPayInfoCfgVO> queryPage(Page page, @Param("queryForm") PaymentChannelPayInfoQueryForm queryForm);


    List<String> queryCurrencyOptions(@Param("departmentId") Long departmentId, @Param("isAdmin") Integer isAdmin);

}
