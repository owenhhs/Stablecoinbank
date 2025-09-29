package net.lab1024.sa.admin.module.business.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderDetailEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderDetailQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderDetailVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderSummaryVO;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderDetailManager;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderInfoManager;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 兑付订单信息表 Service
 *
 * @Author bradydreamer
 * @Date 2025-01-22 22:12:31
 * @Copyright 2025
 */

@Slf4j
@Service
public class CashOrderDetailService {

    @Resource
    private CashOrderInfoManager cashOrderInfoManager;
    @Resource
    private CashOrderDetailManager cashOrderDetailManager;

    public ResponseDTO<CashOrderSummaryVO>  querySubOrderSummary(CashOrderDetailQueryForm queryForm) {
        CashOrderSummaryVO resp = new CashOrderSummaryVO();
        resp.setOrderNo(queryForm.getOrderNo());
        resp.setQueryTime(LocalDateTime.now());

        CashOrderInfoEntity orderInfoEntity = cashOrderInfoManager.queryByOrderNo(queryForm.getOrderNo());
        if (orderInfoEntity == null) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "订单不存在");
        }
        resp.setAmount(orderInfoEntity.getAmount());

        BigDecimal amountSuccessed = cashOrderDetailManager.sumSubOrdersSuccessed(queryForm.getOrderNo());
        if (amountSuccessed.compareTo(BigDecimal.ZERO) < 0) {
            log.error("查询出金订单(orderNo:{})已经成功的子订单的总金额失败！！", queryForm.getOrderNo());
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR);
        }
        resp.setAmountSuccessed(amountSuccessed);

        BigDecimal amountInProgress = cashOrderDetailManager.sumSubOrdersInProgress(queryForm.getOrderNo());
        if (amountInProgress.compareTo(BigDecimal.ZERO) < 0) {
            log.error("查询出金订单(orderNo:{})处理中的子订单的总金额失败！！", queryForm.getOrderNo());
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR);
        }
        resp.setAmountProcessing(amountInProgress);

        BigDecimal amountLeft = orderInfoEntity.getAmount().subtract(amountSuccessed).subtract(amountInProgress);
        resp.setAmountLeft(amountLeft);

        List<CashOrderDetailEntity> listSuccessed = cashOrderDetailManager.querySuccessedSubOrdersByOrderNo(queryForm.getOrderNo());
        resp.setSuccessed(SmartBeanUtil.copyList(listSuccessed, CashOrderDetailVO.class));

        List<CashOrderDetailEntity> listInProgress = cashOrderDetailManager.queryProcessingSubOrdersByOrderNo(queryForm.getOrderNo());
        resp.setProcessing(SmartBeanUtil.copyList(listInProgress, CashOrderDetailVO.class));

        List<CashOrderDetailEntity> listInFailed = cashOrderDetailManager.queryFailedSubOrdersByOrderNo(queryForm.getOrderNo());
        resp.setFailed(SmartBeanUtil.copyList(listInFailed, CashOrderDetailVO.class));

        return ResponseDTO.ok(resp);
    }


}
