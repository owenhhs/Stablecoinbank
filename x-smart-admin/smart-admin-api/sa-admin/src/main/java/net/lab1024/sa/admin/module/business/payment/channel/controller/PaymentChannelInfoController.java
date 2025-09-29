package net.lab1024.sa.admin.module.business.payment.channel.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelParentEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelDetailVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelPayInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.service.PaymentChannelInfoService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道基本信息表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Slf4j
@RestController
@Tag(name = AdminSwaggerTagConst.Business.MERCHANT_MANAGE_PAYMENT_METHOD)
public class PaymentChannelInfoController {

    @Resource
    private PaymentChannelInfoService paymentChannelInfoService;

    @Operation(summary = "查询主商户列表")
    @PostMapping("/paymentChannelInfo/parent/list")
    @SaCheckPermission("channel:parent:query")
    public ResponseDTO<List<PaymentChannelParentEntity>> queryParentChannelList() {
        return ResponseDTO.ok(paymentChannelInfoService.queryParentChannelList());
    }

    @OperateLog
    @Operation(summary = "添加主商户信息")
    @PostMapping("/paymentChannelInfo/parent/add")
    @SaCheckPermission("channel:parent:add")
    public ResponseDTO<String> addParentChannelInfo(@RequestBody PaymentChannelParentAddForm addForm) {
        paymentChannelInfoService.addParentChannelInfo(addForm);
        return ResponseDTO.ok();
    }

    @OperateLog
    @Operation(summary = "更新主商户信息")
    @PostMapping("/paymentChannelInfo/parent/update")
    @SaCheckPermission("channel:parent:update")
    public ResponseDTO<String> updateParentChannelInfo(@RequestBody PaymentChannelParentUpdateForm updateForm) {
        paymentChannelInfoService.updateParentChannelInfo(updateForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "查询子商户列表")
    @PostMapping("/paymentChannelInfo/list")
    public ResponseDTO<List<PaymentChannelInfoVO>> queryChannelList() {
        return ResponseDTO.ok(paymentChannelInfoService.queryPaymentInfoList());
    }

    @OperateLog
    @Operation(summary = "添加商户信息")
    @PostMapping("/paymentChannelInfo/add")
    @SaCheckPermission("channel:info:add")
    public ResponseDTO<String> addChannelInfo(@RequestBody PaymentChannelInfoAddForm addForm) {
        paymentChannelInfoService.addChannelInfo(addForm);
        return ResponseDTO.ok();
    }

    @OperateLog
    @Operation(summary = "更新子商户信息")
    @PostMapping("/paymentChannelInfo/update")
    @SaCheckPermission("channel:info:update")
    public ResponseDTO<String> updateChannelInfo(@RequestBody PaymentChannelInfoUpdateForm updateForm) {
        paymentChannelInfoService.updateChannelInfo(updateForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "查询商户配置详情")
    @PostMapping("/paymentChannelInfo/detail")
    public ResponseDTO<PaymentChannelDetailVO> queryChannelDetail(@RequestBody(required = false) PaymentChannelDetailForm channelDetailForm) {
        return ResponseDTO.ok(paymentChannelInfoService.getChannelDetail(channelDetailForm));
    }

    @Operation(summary = "查询商户支付方式列表接口")
    @PostMapping("/paymentChannelInfo/payinfo/List")
    public ResponseDTO<PageResult<PaymentChannelPayInfoVO>> queryChannelPayinfoList(@RequestBody(required = false) PaymentChannelPayInfoQueryForm queryForm) {
        return ResponseDTO.ok(paymentChannelInfoService.queryChannelPayinfoList(queryForm));
    }

    @Operation(summary = "添加支付方式")
    @PostMapping("/paymentChannelInfo/payinfo/add")
    @OperateLog
    @SaCheckPermission("channel:payment:add")
    public ResponseDTO<String> addPayInfo(@RequestParam(required = false) Long fileId, @RequestParam String type,
                                          @RequestParam String username, @RequestParam(required = false) String bankInfo,
                                          @RequestParam(required = false) String bankName, @RequestParam(required = false) String bankNo,
                                          @RequestParam Integer paymentScale, @RequestParam BigDecimal paymentLimit, @RequestParam Integer paymentCount,
                                          @RequestParam(required = false, defaultValue = "00:00-24:00") String workTime,
                                          @RequestParam BigDecimal amountMin, @RequestParam BigDecimal amountMax,
                                          @RequestParam(required = false) Integer xinjiang,
                                          @RequestParam(required = false) Long departmentId,
                                          @RequestParam String currency, @RequestParam String country) {
        paymentChannelInfoService.addPayInfo(fileId, type, username, bankInfo, bankName, bankNo, paymentScale, paymentLimit,
                paymentCount, workTime, amountMin, amountMax, xinjiang, departmentId, currency, country);
        return ResponseDTO.ok();
    }


    @Operation(summary = "更新支付方式")
    @PostMapping("/paymentChannelInfo/payinfo/update")
    @OperateLog
    @SaCheckPermission("channel:payment:update")
    public ResponseDTO<String> updatePayInfo(@RequestParam Long payInfoId,
                                             @RequestParam(required = false) BigDecimal amountMin,
                                             @RequestParam(required = false) BigDecimal amountMax,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) Long fileId,
                                             @RequestParam(required = false) Integer paymentScale,
                                             @RequestParam(required = false) BigDecimal paymentLimit,
                                             @RequestParam(required = false) Integer paymentCount,
                                             @RequestParam(required = false) String workTime,
                                             @RequestParam(required = false) String username,
                                             @RequestParam(required = false) String bankInfo,
                                             @RequestParam(required = false) String bankName,
                                             @RequestParam(required = false) String bankNo,
                                             @RequestParam(required = false) Integer xinjiang,
                                             @RequestParam(required = false) Long departmentId,
                                             @RequestParam(required = false) String currency,
                                             @RequestParam(required = false) String country) {
        paymentChannelInfoService.updatePayInfo(payInfoId, amountMin, amountMax, status, fileId, paymentScale, paymentLimit,
                paymentCount, workTime, username, bankInfo, bankName, bankNo, xinjiang, departmentId, currency, country);
        return ResponseDTO.ok();
    }

    /**
     * 定时任务跑结算信息
     * 每天凌晨0点重置所有轮询路由计数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void restPollingRouteCountTask() {
        log.info("restPollingRouteCountTask----------------");
        paymentChannelInfoService.restPollingRouteCount();
    }

    @Operation(summary = "查询主商户列表")
    @PostMapping("/paymentChannelInfo/parent/queryOptions")
    public ResponseDTO<List<PaymentChannelParentEntity>> queryParentOptions() {
        return ResponseDTO.ok(paymentChannelInfoService.queryParentOptions());
    }

    @Operation(summary = "查询子商户列表")
    @PostMapping("/paymentChannelInfo/queryOptions")
    public ResponseDTO<List<PaymentChannelInfoVO>> queryOptions() {
        return ResponseDTO.ok(paymentChannelInfoService.queryOptions());
    }

    @Operation(summary = "币种列表")
    @PostMapping("/paymentCurrency/queryOptions")
    public ResponseDTO<List<String>> queryCurrencyOptions() {
        return ResponseDTO.ok(paymentChannelInfoService.queryCurrencyOptions());
    }
}
