package net.lab1024.sa.admin.module.business.payment.channel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentMethodCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentMethodCfgEntity;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付渠道支付方式配置表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Service
public class PaymentMethodCfgService {

//    @Resource
//    private PaymentMethodCfgDao paymentMethodCfgDao;
//
//    /**
//     * 分页查询
//     *
//     * @param queryForm
//     * @return
//     */
//    public PageResult<PaymentMethodCfgVO> queryPage(PaymentMethodCfgQueryForm queryForm) {
//        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
//        List<PaymentMethodCfgVO> list = paymentMethodCfgDao.queryPage(page, queryForm);
//        PageResult<PaymentMethodCfgVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
//        return pageResult;
//    }
//
//    /**
//     * 添加
//     */
//    public ResponseDTO<String> add(PaymentMethodCfgAddForm addForm) {
//        PaymentMethodCfgEntity paymentMethodCfgEntity = SmartBeanUtil.copy(addForm, PaymentMethodCfgEntity.class);
//        paymentMethodCfgDao.insert(paymentMethodCfgEntity);
//        return ResponseDTO.ok();
//    }
//
//    /**
//     * 更新
//     *
//     * @param updateForm
//     * @return
//     */
//    public ResponseDTO<String> update(PaymentMethodCfgUpdateForm updateForm) {
//        PaymentMethodCfgEntity paymentMethodCfgEntity = SmartBeanUtil.copy(updateForm, PaymentMethodCfgEntity.class);
//        paymentMethodCfgDao.updateById(paymentMethodCfgEntity);
//        return ResponseDTO.ok();
//    }
//
//    /**
//     * 批量删除
//     *
//     * @param idList
//     * @return
//     */
//    public ResponseDTO<String> batchDelete(List<Long> idList) {
//        if (CollectionUtils.isEmpty(idList)){
//            return ResponseDTO.ok();
//        }
//
//        paymentMethodCfgDao.deleteBatchIds(idList);
//        return ResponseDTO.ok();
//    }
//
//    /**
//     * 单个删除
//     */
//    public ResponseDTO<String> delete(Long id) {
//        if (null == id){
//            return ResponseDTO.ok();
//        }
//
//        paymentMethodCfgDao.deleteById(id);
//        return ResponseDTO.ok();
//    }
}
