package net.lab1024.sa.admin.module.business.skucfg.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.skucfg.domain.entity.SkuCfgEntity;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgQueryForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.vo.SkuCfgVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 商品配置 Dao
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */

@Mapper
@Component
public interface SkuCfgDao extends BaseMapper<SkuCfgEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SkuCfgVO> queryPage(Page page, @Param("queryForm") SkuCfgQueryForm queryForm);


}
