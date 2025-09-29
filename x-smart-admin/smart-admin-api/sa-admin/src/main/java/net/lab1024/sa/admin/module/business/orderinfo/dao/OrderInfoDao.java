package net.lab1024.sa.admin.module.business.orderinfo.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryExportForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.OrderInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 订单信息表 Dao
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Mapper
@Component
public interface OrderInfoDao extends BaseMapper<OrderInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<OrderInfoVO> queryPage(Page page, @Param("queryForm") OrderInfoQueryForm queryForm);

    List<OrderInfoVO> getExcelExportData(@Param("queryForm") OrderInfoQueryExportForm queryForm);




    List<DashboardOrderTodayVO> queryDashboardOrderToday(@Param("departmentId") Long departmentId, @Param("date") String date);

}
