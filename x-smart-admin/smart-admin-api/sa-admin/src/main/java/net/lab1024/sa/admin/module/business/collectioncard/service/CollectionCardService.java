package net.lab1024.sa.admin.module.business.collectioncard.service;

import java.time.LocalDateTime;
import java.util.List;
import net.lab1024.sa.admin.module.business.collectioncard.dao.CollectionCardDao;
import net.lab1024.sa.admin.module.business.collectioncard.domain.entity.CollectionCardEntity;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardAddForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardQueryForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardUpdateForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardOptionsVO;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 收款银行卡表 Service
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Service
public class CollectionCardService {

    @Resource
    private CollectionCardDao collectionCardDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CollectionCardVO> queryPage(CollectionCardQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CollectionCardVO> list = collectionCardDao.queryPage(page, queryForm);
        PageResult<CollectionCardVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CollectionCardAddForm addForm) {
        CollectionCardEntity collectionCardEntity = SmartBeanUtil.copy(addForm, CollectionCardEntity.class);
        collectionCardEntity.setDeleteFlag(0);
        collectionCardEntity.setCreateTime(LocalDateTime.now());
        collectionCardEntity.setUpdateTime(LocalDateTime.now());
        collectionCardDao.insert(collectionCardEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(CollectionCardUpdateForm updateForm) {
        CollectionCardEntity collectionCardEntity = SmartBeanUtil.copy(updateForm, CollectionCardEntity.class);
        collectionCardEntity.setUpdateTime(LocalDateTime.now());
        collectionCardDao.updateById(collectionCardEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        collectionCardDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        collectionCardDao.deleteById(id);
        return ResponseDTO.ok();
    }


    /**
     * 选项列表
     *
     * @return
     */
    public List<CollectionCardOptionsVO> options() {
        return collectionCardDao.getAllOptions();
    }
}
