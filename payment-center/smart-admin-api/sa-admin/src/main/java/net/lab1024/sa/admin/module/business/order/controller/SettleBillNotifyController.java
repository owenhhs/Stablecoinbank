package net.lab1024.sa.admin.module.business.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleNotifyForm;
import net.lab1024.sa.admin.module.business.order.service.SettleBillNotifyService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 结算回调，渠道中心调用支付中心
 */

@RestController
@Tag(name = "")
public class SettleBillNotifyController {

    @Resource
    private SettleBillNotifyService settleBillNotifyService;

    @Operation(summary = "渠道回调支付中心")
    @PostMapping("/settleInfo/notifySettleUrl")
    public ResponseDTO notifySettleUrl(@RequestBody SettleNotifyForm form, @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date) {
        return settleBillNotifyService.notifySettleOrderUrl(form, authorization, date);
    }

    @Operation(summary = "渠道回调支付中心")
    @PostMapping("/settleInfo/notifySettle")
    public ResponseDTO notifySettle(@RequestBody SettleNotifyForm form, @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date) {
        return settleBillNotifyService.notifySettleOrder(form, authorization, date);
    }

}
