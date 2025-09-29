package net.lab1024.sa.admin.module.business.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderFailRecordEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderFailRecordQueryForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付订单信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentOrderFailRecordDao extends BaseMapper<PaymentOrderFailRecordEntity> {

    List<PaymentOrderFailRecordEntity> queryPage(Page page, @Param("queryForm") PaymentOrderFailRecordQueryForm queryForm);

}
