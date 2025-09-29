package net.lab1024.sa.admin.module.business.payment.channel.service;

import java.time.LocalDateTime;
import java.util.List;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelBusinessCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgUpdateForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelBusinessCfgVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 支付渠道业务范围配置表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Service
public class PaymentChannelBusinessCfgService {

    @Resource
    private PaymentChannelBusinessCfgDao paymentChannelBusinessCfgDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<PaymentChannelBusinessCfgVO> queryPage(PaymentChannelBusinessCfgQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PaymentChannelBusinessCfgVO> list = paymentChannelBusinessCfgDao.queryPage(page, queryForm);
        PageResult<PaymentChannelBusinessCfgVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(PaymentChannelBusinessCfgAddForm addForm) {
        PaymentChannelBusinessCfgEntity paymentChannelBusinessCfgEntity = SmartBeanUtil.copy(addForm, PaymentChannelBusinessCfgEntity.class);
        paymentChannelBusinessCfgEntity.setCreateTime(LocalDateTime.now());
        paymentChannelBusinessCfgEntity.setUpdateTime(LocalDateTime.now());
        paymentChannelBusinessCfgDao.insert(paymentChannelBusinessCfgEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(PaymentChannelBusinessCfgUpdateForm updateForm) {
        PaymentChannelBusinessCfgEntity paymentChannelBusinessCfgEntity = SmartBeanUtil.copy(updateForm, PaymentChannelBusinessCfgEntity.class);
        paymentChannelBusinessCfgEntity.setUpdateTime(LocalDateTime.now());
        paymentChannelBusinessCfgDao.updateById(paymentChannelBusinessCfgEntity);
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

        paymentChannelBusinessCfgDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        paymentChannelBusinessCfgDao.deleteById(id);
        return ResponseDTO.ok();
    }
}
