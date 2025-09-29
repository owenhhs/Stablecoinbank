package net.lab1024.sa.admin.module.business.payment.channel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 渠道基本信息表 Dao
 */

@Mapper
@Component
public interface PaymentChannelInfoDao extends BaseMapper<PaymentChannelInfoEntity> {

//    /**
//     * 分页 查询
//     *
//     * @param page
//     * @param queryForm
//     * @return
//     */
//    List<PaymentChannelInfoVO> queryPage(Page page, @Param("queryForm") PaymentChannelInfoQueryForm queryForm);
//
//
//    List<PaymentChannelInfoVO> queryIdListByAmount(@Param("paymentType") String paymentType, @Param("paymentMethod") String paymentMethod, @Param("paymentChannel") String paymentChannel, @Param("amount") BigDecimal amount);

    PaymentChannelInfoVO queryByDepartmentId(@Param("departmentId") Long departmentId);

    List<PaymentChannelInfoVO> queryOptions(@Param("administratorFlag") Integer administratorFlag, @Param("departmentIds") List<Long> departmentIds);
}
