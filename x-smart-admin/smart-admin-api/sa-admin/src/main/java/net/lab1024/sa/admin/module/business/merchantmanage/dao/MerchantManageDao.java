package net.lab1024.sa.admin.module.business.merchantmanage.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.entity.MerchantManageEntity;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.form.MerchantManageQueryForm;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.vo.MerchantManageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 商户表 Dao
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Mapper
@Component
public interface MerchantManageDao extends BaseMapper<MerchantManageEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<MerchantManageVO> queryPage(Page page, @Param("queryForm") MerchantManageQueryForm queryForm);


}
