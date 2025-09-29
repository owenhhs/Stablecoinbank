package net.lab1024.sa.admin.module.business.dashboard.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/23 23:26
 */
@Data
public class DashboardOrderTodayVO {

    private String channelId;

    private String merCode;

    private String channelName;

    /**
     * 轧差
     */
    private BigDecimal totalAmount;

    /**
     * 总订单数（成功）
     */
    private Integer totalOrderCount;

    /**
     * 入金成功订单总额
     */
    private BigDecimal totalAmountPayment;

    /**
     * 入金成功订单笔数
     */
    private Integer totalOrderCountPayment;

    /**
     * 出金成功订单总额
     */
    private BigDecimal totalAmountCash;

    /**
     * 出金成功订单笔数
     */
    private Integer totalOrderCountCash;
}
