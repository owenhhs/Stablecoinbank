package net.lab1024.sa.admin.module.business.orderinfo.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryExportForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoUpdateForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.OrderInfoVO;
import net.lab1024.sa.admin.module.business.orderinfo.service.OrderInfoService;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.*;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 订单信息表 Controller
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */
@RestController
@Slf4j
@Tag(name = AdminSwaggerTagConst.Business.ORDER_MANAGE_PAYMENT)
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    @Operation(summary = "分页查询 @author sunyu")
    @PostMapping("/orderInfo/queryPage")
    public ResponseDTO<PageResult<OrderInfoVO>> queryPage(@RequestBody @Valid OrderInfoQueryForm queryForm) {
        return ResponseDTO.ok(orderInfoService.queryPage(queryForm));
    }


    @Operation(summary = "导出订单信息")
    @PostMapping("/orderInfo/exportExcel")
    @OperateLog
    public void exportExcel(@RequestBody @Valid OrderInfoQueryExportForm queryForm, HttpServletResponse response) throws IOException {
        List<OrderInfoVO> data = orderInfoService.getExcelExportData(queryForm);
        if (CollectionUtils.isEmpty(data)) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam("暂无数据"));
            return;
        }

        String watermark = AdminRequestUtil.getRequestUser().getActualName();
        watermark += SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);

        SmartExcelUtil.exportExcelWithWatermark(response,"订单列表.xlsx","订单信息",OrderInfoVO.class,data,watermark);

    }

    /**
     * 回调支付中心，在这个接口中
     * @param updateForm
     * @return
     */
    @Operation(summary = "更新 @author sunyu")
    @PostMapping("/orderInfo/update")
    @OperateLog
    @SaCheckPermission("order:info:update")
    public ResponseDTO<String> update(@RequestBody @Valid OrderInfoUpdateForm updateForm) {
        return orderInfoService.update(updateForm);
    }

    @Operation(summary = "Dashboard订单数据 @author sunyu")
    @GetMapping("/dashboard/order-data/today")
    public ResponseDTO<List<DashboardOrderTodayVO>> dashboardOrderDataToday() {
        return orderInfoService.dashboardOrderDataToday();
    }
}
