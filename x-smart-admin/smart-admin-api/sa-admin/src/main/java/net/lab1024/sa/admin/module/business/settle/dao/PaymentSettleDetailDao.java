package net.lab1024.sa.admin.module.business.settle.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleDetailEntity;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleDetailQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleDetailVO;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算表明细表 Dao
 */

@Mapper
@Component
public interface PaymentSettleDetailDao extends BaseMapper<PaymentSettleDetailEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SettleDetailVO> queryPage(Page page, @Param("queryForm") SettleDetailQueryForm queryForm);

}
