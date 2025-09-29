package net.lab1024.sa.admin.module.business.payment.channel.controller;

import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoUpdateForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoOptionVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.service.PaymentChannelInfoService;
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
import java.util.List;

/**
 * 渠道基本信息表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class PaymentChannelInfoController {

    @Resource
    private PaymentChannelInfoService paymentChannelInfoService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/paymentChannelInfo/queryPage")
    public ResponseDTO<PageResult<PaymentChannelInfoVO>> queryPage(@RequestBody @Valid PaymentChannelInfoQueryForm queryForm) {
        return ResponseDTO.ok(paymentChannelInfoService.queryPage(queryForm));
    }

    @OperateLog
    @Operation(summary = "添加 @author Sunny")
    @PostMapping("/paymentChannelInfo/add")
    public ResponseDTO<String> add(@RequestBody @Valid PaymentChannelInfoAddForm addForm) {
        return paymentChannelInfoService.add(addForm);
    }

    @OperateLog
    @Operation(summary = "更新 @author Sunny")
    @PostMapping("/paymentChannelInfo/update")
    public ResponseDTO<String> update(@RequestBody @Valid PaymentChannelInfoUpdateForm updateForm) {
        return paymentChannelInfoService.update(updateForm);
    }

    @OperateLog
    @Operation(summary = "批量删除 @author Sunny")
    @PostMapping("/paymentChannelInfo/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return paymentChannelInfoService.batchDelete(idList);
    }

    @OperateLog
    @Operation(summary = "单个删除 @author Sunny")
    @GetMapping("/paymentChannelInfo/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return paymentChannelInfoService.delete(id);
    }


    @Operation(summary = "支付渠道筛选列表 @author Sunny")
    @PostMapping("/paymentChannelInfo/options")
    public ResponseDTO<List<PaymentChannelInfoOptionVO>> queryOptions() {
        return ResponseDTO.ok(paymentChannelInfoService.queryOptions(1));
    }

    @Operation(summary = "出金渠道筛选列表 @author Sunny")
    @PostMapping("/cashChannelInfo/options")
    public ResponseDTO<List<PaymentChannelInfoOptionVO>> queryCashOptions() {
        return ResponseDTO.ok(paymentChannelInfoService.queryOptions(2));
    }
}
