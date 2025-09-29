package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 结算单表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class SettleBillVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "渠道商户id")
    private Long channelId;

    @Schema(description = "业务范围id")
    private Long businessId;

    @Schema(description = "交易日期")
    private String tradeTime;

    @Schema(description = "交易类型，payment-支付单；cash-兑付单；")
    private String tradeType;

    @Schema(description = "业务范围")
    private String tradeLimit;

    @Schema(description = "交易金额")
    private BigDecimal tradeAmount;

    @Schema(description = "订单笔数")
    private Integer orderCount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "佣金")
    private BigDecimal brokerage;

    @Schema(description = "奖励")
    private BigDecimal award;

    @Schema(description = "结算金额")
    private BigDecimal settleAmount;

    @Schema(description = "结算状态，0 待结算  1 已结算")
    private Integer settleStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}