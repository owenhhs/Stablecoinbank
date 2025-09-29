package net.lab1024.sa.admin.module.business.settle.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleParentEntity;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleExportQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算表 Dao
 */

@Mapper
@Component
public interface PaymentSettleParentDao extends BaseMapper<PaymentSettleParentEntity> {



    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SettleVO> queryPage(Page page, @Param("queryForm") SettleQueryForm queryForm);

    List<SettleVO> getExcelExportData(@Param("queryForm") SettleExportQueryForm queryForm);


}
