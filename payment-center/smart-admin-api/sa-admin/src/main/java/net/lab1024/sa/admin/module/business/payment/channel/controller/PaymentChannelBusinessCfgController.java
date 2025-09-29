package net.lab1024.sa.admin.module.business.payment.channel.controller;

import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelBusinessCfgUpdateForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelBusinessCfgVO;
import net.lab1024.sa.admin.module.business.payment.channel.service.PaymentChannelBusinessCfgService;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * 支付渠道业务范围配置表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class PaymentChannelBusinessCfgController {

    @Resource
    private PaymentChannelBusinessCfgService paymentChannelBusinessCfgService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/paymentChannelBusinessCfg/queryPage")
    public ResponseDTO<PageResult<PaymentChannelBusinessCfgVO>> queryPage(@RequestBody @Valid PaymentChannelBusinessCfgQueryForm queryForm) {
        return ResponseDTO.ok(paymentChannelBusinessCfgService.queryPage(queryForm));
    }

    @OperateLog
    @Operation(summary = "添加 @author Sunny")
    @PostMapping("/paymentChannelBusinessCfg/add")
    public ResponseDTO<String> add(@RequestBody @Valid PaymentChannelBusinessCfgAddForm addForm) {
        return paymentChannelBusinessCfgService.add(addForm);
    }

    @OperateLog
    @Operation(summary = "更新 @author Sunny")
    @PostMapping("/paymentChannelBusinessCfg/update")
    public ResponseDTO<String> update(@RequestBody @Valid PaymentChannelBusinessCfgUpdateForm updateForm) {
        return paymentChannelBusinessCfgService.update(updateForm);
    }

    @OperateLog
    @Operation(summary = "批量删除 @author Sunny")
    @PostMapping("/paymentChannelBusinessCfg/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return paymentChannelBusinessCfgService.batchDelete(idList);
    }

    @OperateLog
    @Operation(summary = "单个删除 @author Sunny")
    @GetMapping("/paymentChannelBusinessCfg/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return paymentChannelBusinessCfgService.delete(id);
    }
}
