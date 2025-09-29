package net.lab1024.sa.admin.module.business.orderinfo.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/23 23:26
 */
@Data
public class DashboardOrderTodayVO {
    private String channelName;

    private String currency;

    private BigDecimal totalAmount;

    private Integer totalOrderCount;
}
