package net.lab1024.sa.admin.module.business.settle.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.settle.domain.form.*;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleDetailVO;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleVO;
import net.lab1024.sa.admin.module.business.settle.service.SettleService;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.*;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 结算表 Controller
 */
@OperateLog
@RestController
@Tag(name = "")
@Slf4j
public class SettleController {

    @Resource
    private SettleService settleService;


    @Operation(summary = "计算某个日期的结算单")
    @PostMapping("/settle/calcByDate")
    @SaCheckPermission("settle:calc:date")
    public ResponseDTO<String> calcByDate(@RequestBody SettleCalcByDateForm settleCalcByDateForm) {
        settleService.deleteSettleItem(settleCalcByDateForm.getSettleDate());
        return ResponseDTO.ok();
    }

    /**
     * 输入汇率，把结算状态改成结算中，并计算相关值
     */
    @Operation(summary = "设置汇率")
    @PostMapping("/settle/setExchangeRate")
    @SaCheckPermission("settle:exchange:set")
    public ResponseDTO<String> setExchangeRate(@RequestBody SettleSetExchangeRateForm settleSetExchangeRateForm) {
        return settleService.setExchangeRate(settleSetExchangeRateForm);
    }

    /**
     * 确认收款，把状态改成已结算
     */
    @Operation(summary = "确认收款")
    @PostMapping("/settle/confirmPayment")
    @SaCheckPermission("settle:payment:confirm")
    public ResponseDTO<String> confirmPayment(@RequestBody SettleSetExchangeRateForm settleSetExchangeRateForm) {
        return settleService.confirmPayment(settleSetExchangeRateForm.getSettleId(), settleSetExchangeRateForm.getFileIds());
    }

    /**
     * 结算列表，带分页
     */
    @Operation(summary = "结算列表")
    @PostMapping("/settle/queryPageBySettle")
    public ResponseDTO<PageResult<SettleVO>> queryPageBySettle(@RequestBody @Valid SettleQueryForm queryForm) {
        return ResponseDTO.ok(settleService.queryPageBySettle(queryForm));
    }


    @Operation(summary = "导出结算列表")
    @PostMapping("/settle/exportExcel")
    public void exportExcel(@RequestBody @Valid SettleExportQueryForm queryForm, HttpServletResponse response) throws IOException {
        List<SettleVO> data = settleService.getExcelExportData(queryForm);
        if (CollectionUtils.isEmpty(data)) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
            return;
        }

        String watermark = AdminRequestUtil.getRequestUser().getActualName();
        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);

        SmartExcelUtil.exportExcelWithWatermark(response, "结算列表.xlsx", "结算信息", SettleVO.class, data, watermark);

    }


    /**
     * 结算明细列表，带分页
     */
    @Operation(summary = "结算明细列表")
    @PostMapping("/settle/queryPageBySettleDetail")
    public ResponseDTO<PageResult<SettleDetailVO>> queryPageBySettleDetail(@RequestBody @Valid SettleDetailQueryForm queryForm) {
        return ResponseDTO.ok(settleService.queryPageBySettleDetail(queryForm));
    }


//    /**
//     * 输入汇率，把结算状态改成结算中，并计算相关值
//     */
//    @Operation(summary = "设置汇率")
//    @PostMapping("/settle/parent/setExchangeRate")
//    @SaCheckPermission("settle:parent:exchange:set")
//    public ResponseDTO<String> setParentExchangeRate(@RequestBody SettleSetExchangeRateForm settleSetExchangeRateForm) {
//        return settleService.setParentExchangeRate(settleSetExchangeRateForm);
//    }

//    /**
//     * 确认收款，把状态改成已结算
//     */
//    @Operation(summary = "确认收款")
//    @PostMapping("/settle/parent/confirmPayment")
//    @SaCheckPermission("settle:parent:payment:confirm")
//    public ResponseDTO<String> confirmParentPayment(@RequestBody SettleSetExchangeRateForm settleSetExchangeRateForm) {
//        return settleService.confirmParentPayment(settleSetExchangeRateForm.getSettleId(), settleSetExchangeRateForm.getFileIds());
//    }

    /**
     * 结算列表，带分页
     */
    @Operation(summary = "结算列表")
    @PostMapping("/settle/parent/queryPageBySettle")
    @SaCheckPermission("settle:parent:query")
    public ResponseDTO<PageResult<SettleVO>> queryPageByParentSettle(@RequestBody @Valid SettleQueryForm queryForm) {
        return ResponseDTO.ok(settleService.queryPageByParentSettle(queryForm));
    }


    @Operation(summary = "导出结算列表")
    @PostMapping("/settle/parent/exportExcel")
    @SaCheckPermission("settle:parent:export")
    public void exportParentExcel(@RequestBody @Valid SettleExportQueryForm queryForm, HttpServletResponse response) throws IOException {
        List<SettleVO> data = settleService.getParentExcelExportData(queryForm);
        if (CollectionUtils.isEmpty(data)) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
            return;
        }

        String watermark = AdminRequestUtil.getRequestUser().getActualName();
        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);

        SmartExcelUtil.exportExcelWithWatermark(response, "结算列表.xlsx", "结算信息", SettleVO.class, data, watermark);

    }

    /**
     * 结算明细列表，带分页
     */
    @Operation(summary = "结算明细列表")
    @PostMapping("/settle/parent/queryPageBySettleDetail")
    @SaCheckPermission("settle:parent:detail")
    public ResponseDTO<PageResult<SettleDetailVO>> queryPageByParentSettleDetail(@RequestBody @Valid SettleDetailQueryForm queryForm) {
        return ResponseDTO.ok(settleService.queryPageByParentSettleDetail(queryForm));
    }

}
