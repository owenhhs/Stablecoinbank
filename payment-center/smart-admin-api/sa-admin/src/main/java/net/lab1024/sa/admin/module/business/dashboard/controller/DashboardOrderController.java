package net.lab1024.sa.admin.module.business.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.dashboard.service.DashboardOrderService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 孙宇
 * @date 2024/09/23 23:23
 */
@RestController
@Tag(name = "")
public class DashboardOrderController {
    @Resource
    private DashboardOrderService dashboardOrderService;

    @Operation(summary = "订单数据 @author sunyu")
    @GetMapping("/dashboard/order-data/today")
    public ResponseDTO<List<DashboardOrderTodayVO>> dashboardOrderDataToday() {
        return dashboardOrderService.dashboardOrderDataToday();
    }
}
