package net.lab1024.sa.admin.module.business.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderFailRecordEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderFailRecordQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoExportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoReceiptForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import net.lab1024.sa.admin.module.business.order.service.PaymentOrderInfoService;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.util.*;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
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
 * 支付订单信息表 Controller
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@RestController
@Tag(name = "")
public class PaymentOrderInfoController {

    @Resource
    private PaymentOrderInfoService paymentOrderInfoService;

    @Operation(summary = "分页查询 @author Sunny")
    @PostMapping("/orderInfo/queryPage")
    public ResponseDTO<PageResult<PaymentOrderInfoListVO>> queryPage(@RequestBody @Valid PaymentOrderInfoQueryForm queryForm) {
        return ResponseDTO.ok(paymentOrderInfoService.queryPage(queryForm));
    }


    @OperateLog
    @Operation(summary = "导出订单信息")
    @PostMapping("/orderInfo/exportExcel")
    @SaCheckPermission("order:list:export")
    public void exportExcel(@RequestBody @Valid PaymentOrderInfoExportQueryForm queryForm, HttpServletResponse response) throws IOException {
        long counted = paymentOrderInfoService.countExcelExportData(queryForm);
        if (counted == 0L) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
            return;
        }

        String watermark = AdminRequestUtil.getRequestUser().getActualName();
        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);

        SmartExcelUtil.exportExcelBlockWithWatermark(response, "订单列表.xlsx", "订单信息", PaymentOrderInfoListVO.class, page -> {
            queryForm.setPageNum(page);
            queryForm.setPageSize(10000L);
            return paymentOrderInfoService.getExcelExportData(queryForm);
        }, watermark);

    }

    @OperateLog
    @Operation(summary = "回单补充 @author sunyu")
    @PostMapping("/orderInfo/receipt/upload")
    @SaCheckPermission("order:receipt:upload")
    public ResponseDTO<String> orderReceiptUpload(@RequestBody PaymentOrderInfoReceiptForm form) {
        return paymentOrderInfoService.orderReceiptUpload(form);
    }


    @Operation(summary = "失败订单记录查询 @author sunyu")
    @PostMapping("/orderInfo/fail/record")
    @SaCheckPermission("order:fail:record")
    public ResponseDTO<PageResult<PaymentOrderFailRecordEntity>> queryOrderFailRecord(@Valid @RequestBody PaymentOrderFailRecordQueryForm form) {
        return ResponseDTO.ok(paymentOrderInfoService.queryOrderFailRecord(form));
    }

}
