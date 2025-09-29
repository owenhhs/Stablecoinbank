package net.lab1024.sa.admin.module.business.payment.channel.dao;

import java.math.BigDecimal;
import java.util.List;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 渠道基本信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentChannelInfoDao extends BaseMapper<PaymentChannelInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PaymentChannelInfoVO> queryPage(Page page, @Param("queryForm") PaymentChannelInfoQueryForm queryForm);


    List<PaymentChannelInfoVO> queryIdListByAmount(@Param("paymentType") String paymentType,
                                                   @Param("paymentMethod") String paymentMethod,
                                                   @Param("paymentChannel") String paymentChannel,
                                                   @Param("amount") BigDecimal amount,
                                                   @Param("currency") String currency,
                                                   @Param("country") String country,
                                                   @Param("xjFlag") int xjFlag,
                                                   @Param("filterFxmch") boolean filterFxmch
                                                   );


    List<PaymentChannelInfoVO> queryListByIds(@Param("ids") List<Long> ids, @Param("paymentType") String paymentType,
                                              @Param("paymentMethod") String paymentMethod,
                                              @Param("paymentChannel") String paymentChannel,
                                              @Param("amount") BigDecimal amount,
                                              @Param("currency") String currency,
                                              @Param("country") String country,
                                              @Param("xjFlag") int xjFlag
                                              );

    PaymentChannelInfoVO queryById(@Param("id") Long id);

}
