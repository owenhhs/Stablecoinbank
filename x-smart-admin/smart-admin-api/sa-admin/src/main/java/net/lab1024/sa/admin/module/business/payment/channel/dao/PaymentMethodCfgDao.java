package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentMethodCfgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付渠道支付方式配置表 Dao
 */

@Mapper
@Component
public interface PaymentMethodCfgDao extends BaseMapper<PaymentMethodCfgEntity> {

//    /**
//     * 分页 查询
//     *
//     * @param page
//     * @param queryForm
//     * @return
//     */
//    List<PaymentMethodCfgVO> queryPage(Page page, @Param("queryForm") PaymentMethodCfgQueryForm queryForm);


}
