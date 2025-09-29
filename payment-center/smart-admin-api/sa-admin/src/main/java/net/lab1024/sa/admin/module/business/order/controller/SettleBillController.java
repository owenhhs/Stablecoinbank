package net.lab1024.sa.admin.module.business.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import net.lab1024.sa.admin.module.business.order.domain.form.*;
import net.lab1024.sa.admin.module.business.order.domain.vo.SettleBillListVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.SettleVO;
import net.lab1024.sa.admin.module.business.order.service.SettleBillService;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 结算单表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class SettleBillController {

    @Resource
    private SettleBillService settleBillService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/settleInfo/queryPage")
    public ResponseDTO<PageResult<SettleBillListVO>> queryPage(@RequestBody @Valid SettleBillQueryForm queryForm) {
        return ResponseDTO.ok(settleBillService.queryPage(queryForm));
    }



    @Operation(summary = "导出结算信息")
    @PostMapping("/settleInfo/exportExcel")
    @SaCheckPermission("settle:order:export")
    public void exportExcel(@RequestBody @Valid SettleBillExportQueryForm queryForm, HttpServletResponse response) throws IOException {
        long counted = settleBillService.countExcelExportData(queryForm);
        if (counted == 0L) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
            return;
        }

        String watermark = AdminRequestUtil.getRequestUser().getActualName();
        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);

        SmartExcelUtil.exportExcelBlockWithWatermark(response, "结算列表.xlsx", "结算信息", SettleBillListVO.class, page -> {
            queryForm.setPageNum(page);
            queryForm.setPageSize(10000L);
            return settleBillService.getExcelExportData(queryForm);
        }, watermark);
    }


    @Operation(summary = "更新 @author Sunny")
    @PostMapping("/settleInfo/exchangeRate")
    @SaCheckPermission("settle:order:update")
    public ResponseDTO<String> update(@RequestBody @Valid SettleBillUpdateForm updateForm) {
        return settleBillService.update(updateForm);
    }

    @Operation(summary = "结算完成 @author Sunny")
    @PostMapping("/settleInfo/finished")
    @SaCheckPermission("settle:order:finished")
    public ResponseDTO<String> finished(@RequestBody @Valid SettleBillFinishedForm updateForm) {
        return settleBillService.finished(updateForm);
    }


    @Operation(summary = "结算单重算 @author Sunny")
    @PostMapping("/settle/calcByDate")
    @SaCheckPermission("settle:order:recalculation")
    public ResponseDTO<String> recalculation(@RequestBody @Valid SettleBillRecalcForm recalcForm) {
        return settleBillService.recalculation(recalcForm);
    }



    /**
     * 结算列表，带分页
     */
    @Operation(summary = "结算列表")
    @PostMapping("/settleInfo/channel/queryPage")
    @SaCheckPermission("settle:order:channel")
    public ResponseDTO<PageResult<SettleVO>> queryPageBySettle(@RequestBody @Valid SettleQueryForm queryForm) {
        return settleBillService.queryPageBySettle(queryForm);
    }

}
