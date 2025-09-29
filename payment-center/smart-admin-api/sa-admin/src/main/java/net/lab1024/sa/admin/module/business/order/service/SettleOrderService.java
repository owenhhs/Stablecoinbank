package net.lab1024.sa.admin.module.business.order.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.dao.SettleOrderDao;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleOrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 结算单订单关联表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */

@Service
public class SettleOrderService {

    @Resource
    private SettleOrderDao settleOrderDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<PaymentOrderInfoListVO> queryPage(SettleOrderQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PaymentOrderInfoListVO> list = settleOrderDao.queryPage(page, queryForm);
        PageResult<PaymentOrderInfoListVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }
}
