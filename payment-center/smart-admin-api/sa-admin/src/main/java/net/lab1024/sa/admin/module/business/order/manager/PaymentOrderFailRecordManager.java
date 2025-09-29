package net.lab1024.sa.admin.module.business.order.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderFailRecordDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderFailRecordEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderFailRecordQueryForm;
import net.lab1024.sa.base.common.util.LogIdUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 孙宇
 * @date 2025/04/10 22:05
 */
@Service
public class PaymentOrderFailRecordManager  extends ServiceImpl<PaymentOrderFailRecordDao, PaymentOrderFailRecordEntity> {

    public void savePaymentOrderFailRecord(String orderNo, String params, String failMsg) {
        PaymentOrderFailRecordEntity paymentOrderFailRecordEntity = new PaymentOrderFailRecordEntity();
        paymentOrderFailRecordEntity.setOrderNo(orderNo);
        paymentOrderFailRecordEntity.setParams(params);
        paymentOrderFailRecordEntity.setFailMsg(failMsg);
        paymentOrderFailRecordEntity.setRequestId(LogIdUtil.getRequestId());
        paymentOrderFailRecordEntity.setCreateTime(LocalDateTime.now());
        paymentOrderFailRecordEntity.setUpdateTime(LocalDateTime.now());
        this.save(paymentOrderFailRecordEntity);
    }

    public List<PaymentOrderFailRecordEntity> queryPage(Page page, PaymentOrderFailRecordQueryForm form) {
        return this.baseMapper.queryPage(page, form);
    }


}
