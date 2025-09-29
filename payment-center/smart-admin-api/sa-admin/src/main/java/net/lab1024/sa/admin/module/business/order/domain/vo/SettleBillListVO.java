package net.lab1024.sa.admin.module.business.order.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 结算单表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class SettleBillListVO {


    @ExcelIgnore
    @Schema(description = "主键")
    private Long id;

    @ExcelProperty("商戶名称")
    @Schema(description = "商户名称")
    private String merName;

    @ExcelProperty("商戶编号")
    @Schema(description = "商户编号")
    private String merNo;

    @ExcelProperty("交易日期")
    @Schema(description = "交易日期")
    private String tradeTime;

    @ExcelProperty("交易类型")
    @Schema(description = "交易类型，payment-支付单；cash-兑付单；")
    private String tradeType;

    @ExcelProperty("业务范围")
    @Schema(description = "业务范围")
    private String tradeLimit;

    @ExcelProperty("交易金额")
    @Schema(description = "交易金额")
    private BigDecimal tradeAmount;

    @ExcelProperty("笔数")
    @Schema(description = "订单笔数")
    private Integer orderCount;

    @ExcelProperty("币种")
    @Schema(description = "币种")
    private String currency;

    @ExcelProperty("汇率")
    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("佣金")
    @Schema(description = "佣金")
    private BigDecimal brokerage;

    @ExcelProperty("奖励")
    @Schema(description = "奖励")
    private BigDecimal award;

    @ExcelProperty("结算金额")
    @Schema(description = "结算金额")
    private BigDecimal settleAmount;

    @ExcelIgnore
    @Schema(description = "结算状态，0 待结算  1 已结算")
    private Integer settleStatus;

    @ExcelProperty("结算状态")
    @Schema(description = "结算状态，0 待结算  1 已结算")
    private String settleStatusStr;

    @Schema(description = "结算凭证")
    private String settleUrl;

}