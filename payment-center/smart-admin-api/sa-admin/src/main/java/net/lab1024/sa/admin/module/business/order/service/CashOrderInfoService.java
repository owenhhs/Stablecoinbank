package net.lab1024.sa.admin.module.business.order.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderInfoVO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 兑付订单信息表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Service
public class CashOrderInfoService {

    @Resource
    private CashOrderInfoDao cashOrderInfoDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CashOrderInfoVO> queryPage(CashOrderInfoQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CashOrderInfoVO> list = cashOrderInfoDao.queryPage(page, queryForm);
        PageResult<CashOrderInfoVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }
}
