package net.lab1024.sa.admin.module.business.order.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderDetailEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderDetailQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 兑付订单信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface CashOrderDetailDao extends BaseMapper<CashOrderDetailEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CashOrderDetailVO> queryPage(Page page, @Param("queryForm") CashOrderDetailQueryForm queryForm);

    List<DashboardOrderTodayVO> queryDashboardOrderToday();

}
