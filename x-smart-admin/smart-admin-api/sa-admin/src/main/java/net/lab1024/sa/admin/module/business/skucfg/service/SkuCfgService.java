package net.lab1024.sa.admin.module.business.skucfg.service;

import java.time.LocalDateTime;
import java.util.List;
import net.lab1024.sa.admin.module.business.skucfg.dao.SkuCfgDao;
import net.lab1024.sa.admin.module.business.skucfg.domain.entity.SkuCfgEntity;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgAddForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgQueryForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgUpdateForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.vo.SkuCfgVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品配置 Service
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */

@Service
public class SkuCfgService {

    @Resource
    private SkuCfgDao skuCfgDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SkuCfgVO> queryPage(SkuCfgQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SkuCfgVO> list = skuCfgDao.queryPage(page, queryForm);
        PageResult<SkuCfgVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SkuCfgAddForm addForm) {
        SkuCfgEntity skuCfgEntity = SmartBeanUtil.copy(addForm, SkuCfgEntity.class);
        skuCfgEntity.setDeleteFlag(0);
        skuCfgEntity.setCreateTime(LocalDateTime.now());
        skuCfgEntity.setUpdateTime(LocalDateTime.now());
        skuCfgDao.insert(skuCfgEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SkuCfgUpdateForm updateForm) {
        SkuCfgEntity skuCfgEntity = SmartBeanUtil.copy(updateForm, SkuCfgEntity.class);
        skuCfgEntity.setUpdateTime(LocalDateTime.now());
        skuCfgDao.updateById(skuCfgEntity);
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

        skuCfgDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        skuCfgDao.deleteById(id);
        return ResponseDTO.ok();
    }
}
