package net.lab1024.sa.admin.module.business.merchantmanage.service;

import java.time.LocalDateTime;
import java.util.List;
import net.lab1024.sa.base.module.support.merchant.dao.MerchantManageDao;
import net.lab1024.sa.base.module.support.merchant.domain.entity.MerchantManageEntity;
import net.lab1024.sa.base.module.support.merchant.domain.form.MerchantManageAddForm;
import net.lab1024.sa.base.module.support.merchant.domain.form.MerchantManageQueryForm;
import net.lab1024.sa.base.module.support.merchant.domain.form.MerchantManageUpdateForm;
import net.lab1024.sa.base.module.support.merchant.domain.vo.MerchantManageVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.module.support.merchant.domain.vo.MerchantOptionVO;
import net.lab1024.sa.base.module.support.merchant.manager.MerchantManageManager;
import net.lab1024.sa.base.module.support.serialnumber.constant.SerialNumberIdEnum;
import net.lab1024.sa.base.module.support.serialnumber.service.SerialNumberService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商户表 Service
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Service
public class MerchantManageService {

    @Resource
    private MerchantManageDao merchantManageDao;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private MerchantManageManager merchantManageManager;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<MerchantManageVO> queryPage(MerchantManageQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<MerchantManageVO> list = merchantManageDao.queryPage(page, queryForm);
        PageResult<MerchantManageVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    public List<MerchantOptionVO> getOptions() {
        return merchantManageManager.getMerchantOptions();
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(MerchantManageAddForm addForm) {
        MerchantManageEntity merchantManageEntity = SmartBeanUtil.copy(addForm, MerchantManageEntity.class);
        String merchantNo = serialNumberService.generate(SerialNumberIdEnum.MERCHANT);
        merchantManageEntity.setMerNo(merchantNo);
        merchantManageEntity.setSecretKey(RandomStringUtils.randomAlphanumeric(32));
        merchantManageEntity.setCreateTime(LocalDateTime.now());
        merchantManageEntity.setUpdateTime(LocalDateTime.now());
        merchantManageDao.insert(merchantManageEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(MerchantManageUpdateForm updateForm) {
        MerchantManageEntity merchantManageEntity = SmartBeanUtil.copy(updateForm, MerchantManageEntity.class);
        merchantManageEntity.setUpdateTime(LocalDateTime.now());
        merchantManageDao.updateById(merchantManageEntity);
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

        merchantManageDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        merchantManageDao.deleteById(id);
        return ResponseDTO.ok();
    }
}
