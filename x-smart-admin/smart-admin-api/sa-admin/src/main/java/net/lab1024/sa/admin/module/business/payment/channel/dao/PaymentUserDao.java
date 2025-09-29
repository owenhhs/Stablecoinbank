package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentUserEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentUserQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付用户表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentUserDao extends BaseMapper<PaymentUserEntity> {

    List<PaymentUserVO> queryPage(Page page, @Param("queryForm") PaymentUserQueryForm queryForm);
}
