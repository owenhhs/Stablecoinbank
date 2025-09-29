package net.lab1024.sa.admin.module.business.payment.channel.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentMethodCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentMethodCfgQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentMethodCfgVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 支付渠道支付方式配置表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentMethodCfgDao extends BaseMapper<PaymentMethodCfgEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PaymentMethodCfgVO> queryPage(Page page, @Param("queryForm") PaymentMethodCfgQueryForm queryForm);


}
