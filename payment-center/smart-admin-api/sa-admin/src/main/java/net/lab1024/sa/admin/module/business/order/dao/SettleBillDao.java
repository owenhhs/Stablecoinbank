package net.lab1024.sa.admin.module.business.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleBillEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleBillExportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleBillQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.SettleBillListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算单表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Mapper
@Component
public interface SettleBillDao extends BaseMapper<SettleBillEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SettleBillListVO> queryPage(Page page, @Param("queryForm") SettleBillQueryForm queryForm);

    List<SettleBillListVO> getExcelExportData(Page page, @Param("queryForm") SettleBillExportQueryForm queryForm);

    long countExcelExportData(@Param("queryForm") SettleBillExportQueryForm queryForm);


}
