package net.lab1024.sa.admin.module.business.order.dao;

import java.util.List;

import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoExportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderCountVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 支付订单信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface PaymentOrderInfoDao extends BaseMapper<PaymentOrderInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PaymentOrderInfoListVO> queryPage(Page page, @Param("queryForm") PaymentOrderInfoQueryForm queryForm);

    List<PaymentOrderInfoListVO> getExcelExportData(Page page, @Param("queryForm") PaymentOrderInfoExportQueryForm queryForm);

    long countExcelExportData(@Param("queryForm") PaymentOrderInfoExportQueryForm queryForm);


    /**
     * 获取当日订单数量
     * @return
     */
    List<OrderCountVO> queryTodayOrderCount(@Param("ids") List<Long> ids);


    List<DashboardOrderTodayVO> queryDashboardOrderToday();

}
