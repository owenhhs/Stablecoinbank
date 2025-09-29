package net.lab1024.sa.admin.module.business.order.controller;

import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderDetailQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderDetailVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderInfoVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderSummaryVO;
import net.lab1024.sa.admin.module.business.order.service.CashOrderDetailService;
import net.lab1024.sa.admin.module.business.order.service.CashOrderInfoService;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawVO;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import net.lab1024.sa.base.common.code.ErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 兑付订单信息表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class CashOrderInfoController {

    @Resource
    private CashOrderInfoService cashOrderInfoService;
    @Resource
    private CashOrderDetailService cashOrderDetailService;

    @Resource
    private OrderService orderService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/cashOrderInfo/queryPage")
    public ResponseDTO<PageResult<CashOrderInfoVO>> queryPage(@RequestBody @Valid CashOrderInfoQueryForm queryForm) {
        return ResponseDTO.ok(cashOrderInfoService.queryPage(queryForm));
    }

    @Operation(summary = "人工处理 @author Sunny")
    @PostMapping("/cashOrderInfo/manual")
    public ResponseDTO<OrderWithdrawVO> manualWithdraw(@RequestBody @Valid CashOrderManualForm form) throws Exception {
        return orderService.manualWithdraw(form);
    }

    @Operation(summary = "分页查询 @author bradydreamer")
    @PostMapping("/cashOrderDetail/querySubOrderSummary")
    public ResponseDTO<CashOrderSummaryVO> querySubOrderSummary(@RequestBody @Valid CashOrderDetailQueryForm queryForm) {
        return cashOrderDetailService.querySubOrderSummary(queryForm);
    }
}
