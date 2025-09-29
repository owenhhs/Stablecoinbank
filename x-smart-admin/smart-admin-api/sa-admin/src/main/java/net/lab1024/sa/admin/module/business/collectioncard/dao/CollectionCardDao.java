package net.lab1024.sa.admin.module.business.collectioncard.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.collectioncard.domain.entity.CollectionCardEntity;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardQueryForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardOptionsVO;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 收款银行卡表 Dao
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Mapper
@Component
public interface CollectionCardDao extends BaseMapper<CollectionCardEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CollectionCardVO> queryPage(Page page, @Param("queryForm") CollectionCardQueryForm queryForm);


    List<CollectionCardOptionsVO> getAllOptions();


}
