package net.lab1024.sa.admin.module.business.payment.channel.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelBusinessCfgVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 支付渠道业务范围配置表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentChannelBusinessCfgDao extends BaseMapper<PaymentChannelBusinessCfgEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PaymentChannelBusinessCfgVO> queryPage(Page page, @Param("queryForm") PaymentChannelBusinessCfgQueryForm queryForm);


}
