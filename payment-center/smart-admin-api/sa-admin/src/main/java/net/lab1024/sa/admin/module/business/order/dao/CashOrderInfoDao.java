package net.lab1024.sa.admin.module.business.order.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 兑付订单信息表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface CashOrderInfoDao extends BaseMapper<CashOrderInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CashOrderInfoVO> queryPage(Page page, @Param("queryForm") CashOrderInfoQueryForm queryForm);

    /**
     * 获取当日订单数量
     * @return
     */
    List<OrderCountVO> queryTodayOrderCount(@Param("ids") List<Long> ids);
}
