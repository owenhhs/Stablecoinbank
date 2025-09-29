package net.lab1024.sa.admin.module.business.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.dashboard.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderDetailManager;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 孙宇
 * @date 2024/09/23 23:23
 */
@Slf4j
@Service
public class DashboardOrderService {

    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private CashOrderDetailManager cashOrderDetailManager;

    public ResponseDTO<List<DashboardOrderTodayVO>> dashboardOrderDataToday() {
        //获取入金订单汇总
        List<DashboardOrderTodayVO> dashboardOrderTodayVOList = paymentOrderInfoManager.queryDashboardOrderTodayList();

        //获取出金订单汇总
        List<DashboardOrderTodayVO> cashOrderTodayVOList = cashOrderDetailManager.queryDashboardOrderTodayList();

        for (DashboardOrderTodayVO todayVO : dashboardOrderTodayVOList) {
            for (DashboardOrderTodayVO cashVO : cashOrderTodayVOList) {
                if (cashVO.getChannelId().equals(todayVO.getChannelId())) {
                    todayVO.setTotalAmountCash(cashVO.getTotalAmountCash());
                    todayVO.setTotalOrderCountCash(cashVO.getTotalOrderCountCash());

                    todayVO.setTotalOrderCount(todayVO.getTotalOrderCountPayment() + todayVO.getTotalOrderCountCash());
                    todayVO.setTotalAmount(todayVO.getTotalAmountPayment().subtract(todayVO.getTotalAmountCash()));
                    break;
                }
            }
        }

        return ResponseDTO.ok(dashboardOrderTodayVOList);
    }
}
