package net.lab1024.sa.admin.module.business.payment.channel.controller;

import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentMethodCfgAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentMethodCfgQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentMethodCfgUpdateForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentMethodCfgVO;
import net.lab1024.sa.admin.module.business.payment.channel.service.PaymentMethodCfgService;
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
 * 支付渠道支付方式配置表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class PaymentMethodCfgController {

    @Resource
    private PaymentMethodCfgService paymentMethodCfgService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/paymentMethodCfg/queryPage")
    public ResponseDTO<PageResult<PaymentMethodCfgVO>> queryPage(@RequestBody @Valid PaymentMethodCfgQueryForm queryForm) {
        return ResponseDTO.ok(paymentMethodCfgService.queryPage(queryForm));
    }

    @OperateLog
    @Operation(summary = "添加 @author Sunny")
    @PostMapping("/paymentMethodCfg/add")
    public ResponseDTO<String> add(@RequestBody @Valid PaymentMethodCfgAddForm addForm) {
        return paymentMethodCfgService.add(addForm);
    }

    @OperateLog
    @Operation(summary = "更新 @author Sunny")
    @PostMapping("/paymentMethodCfg/update")
    public ResponseDTO<String> update(@RequestBody @Valid PaymentMethodCfgUpdateForm updateForm) {
        return paymentMethodCfgService.update(updateForm);
    }

    @OperateLog
    @Operation(summary = "批量删除 @author Sunny")
    @PostMapping("/paymentMethodCfg/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return paymentMethodCfgService.batchDelete(idList);
    }

    @OperateLog
    @Operation(summary = "单个删除 @author Sunny")
    @GetMapping("/paymentMethodCfg/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return paymentMethodCfgService.delete(id);
    }
}
