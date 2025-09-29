package net.lab1024.sa.admin.module.business.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderManualImportEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualImportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderInfoVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderManualImportVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 兑付订单人工导入信息表 Dao
 */

@Mapper
@Component
public interface CashOrderManualImportDao extends BaseMapper<CashOrderManualImportEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CashOrderManualImportVO> queryPage(Page page, @Param("queryForm") CashOrderManualImportQueryForm queryForm);

}
