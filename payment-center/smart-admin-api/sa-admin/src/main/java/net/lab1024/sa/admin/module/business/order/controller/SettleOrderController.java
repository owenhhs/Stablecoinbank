package net.lab1024.sa.admin.module.business.order.controller;

import net.lab1024.sa.admin.module.business.order.domain.form.SettleOrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import net.lab1024.sa.admin.module.business.order.service.SettleOrderService;
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
 * 结算单订单关联表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class SettleOrderController {

    @Resource
    private SettleOrderService settleOrderService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/settleInfo/orderList")
    public ResponseDTO<PageResult<PaymentOrderInfoListVO>> queryPage(@RequestBody @Valid SettleOrderQueryForm queryForm) {
        return ResponseDTO.ok(settleOrderService.queryPage(queryForm));
    }
}
