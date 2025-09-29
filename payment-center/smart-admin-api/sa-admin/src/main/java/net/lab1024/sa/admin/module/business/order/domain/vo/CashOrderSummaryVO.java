package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 兑付订单信息表 列表VO
 *
 * @Author bradydreamer
 * @Date 2025-01-22 22:12:31
 * @Copyright 2025
 */

@Data
public class CashOrderSummaryVO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "查询时间")
    private LocalDateTime queryTime;

    @Schema(description = "出金金额")
    private BigDecimal amount;

    @Schema(description = "已成功出金金额")
    private BigDecimal amountSuccessed;

    @Schema(description = "已成功出金金额")
    private BigDecimal amountProcessing;

    @Schema(description = "剩余出金金额")
    private BigDecimal amountLeft;

    @Schema(description = "已成功出金列表")
    private List<CashOrderDetailVO> successed;

    @Schema(description = "出金中列表")
    private List<CashOrderDetailVO> processing;

    @Schema(description = "出金失败列表")
    private List<CashOrderDetailVO> failed;


}