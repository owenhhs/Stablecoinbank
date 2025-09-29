package net.lab1024.sa.admin.module.business.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualImportForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualImportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderManualImportVO;
import net.lab1024.sa.admin.module.business.order.service.CashOrderManualImportService;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawBatchImportVO;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawVO;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 兑付订单手工导入表 Controller
 */

@RestController
@Tag(name = "")
public class CashOrderManualImportController {

    @Resource
    private CashOrderManualImportService cashOrderManualImportService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/cashOrderManualImport/queryPage")
    public ResponseDTO<PageResult<CashOrderManualImportVO>> queryPage(@RequestBody @Valid CashOrderManualImportQueryForm queryForm) {
        return ResponseDTO.ok(cashOrderManualImportService.queryPage(queryForm));
    }

    @Operation(summary = "人工导入待出金订单")
    @PostMapping("/cashOrderManualImport/importExcel")
    public ResponseDTO<OrderWithdrawBatchImportVO> manualImport(@RequestBody @Valid List<CashOrderManualImportForm> form) throws Exception {
        System.out.println("manualImport ==> " + JSON.toJSONString(form));
        return cashOrderManualImportService.batchImport(form);
    }

    @Operation(summary = "导出出金订单信息")
    @PostMapping("/cashOrderManualImport/exportExcel")
    public void exportExcel(@RequestBody @Valid CashOrderManualImportQueryForm queryForm, HttpServletResponse response) throws IOException {
//        List<SettleBillListVO> data = cashOrderManualImportService.getExcelExportData(queryForm);
//        if (CollectionUtils.isEmpty(data)) {
//            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
//            return;
//        }
//
//        String watermark = AdminRequestUtil.getRequestUser().getActualName();
//        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);
//
//        SmartExcelUtil.exportExcelWithWatermark(response,"结算列表.xlsx","结算信息",SettleBillListVO.class,data,watermark);

    }

    @Operation(summary = "出金订单回调接口（专门针对手工导入订单）")
    @PostMapping("/notify/manual/withdraw/order/result")
    public Map<String, Object> notifyManualImportWithdrawOrder(@RequestBody Map<String, Object> params) {
        return cashOrderManualImportService.notifyOrderResult(params);
    }

}
