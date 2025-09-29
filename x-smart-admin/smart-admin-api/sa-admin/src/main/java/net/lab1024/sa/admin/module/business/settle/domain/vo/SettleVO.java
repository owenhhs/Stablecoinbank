package net.lab1024.sa.admin.module.business.settle.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 结算vo
 */

@Data
public class SettleVO {
    /**
     * 主键
     */
    @ExcelIgnore
    private Long id;

    /**
     * 部门id
     */
    @ExcelIgnore
    private Long departmentId;

    /**
     * 结算状态 1:待结算，2:结算中，3:待确认，4:已结算
     */
    @ExcelIgnore
    private int  settleStatus;

    @ExcelProperty("结算状态")
    private String settleStatusStr;
    /**
     * 商户编码
     */
    @ExcelProperty("商户编号")
    private String merCode;

    /**
     * 商户名称
     */
    @ExcelProperty("商户名称")
    private String merName;

    /**
     * 交易日期（年-月-日）
     */
    @ExcelProperty("交易日期")
    private String tradeDate;

    /**
     * 交易类型1：支付单，2兑付单
     */
    @ExcelIgnore
    private int tradeType;

    @ExcelProperty("交易类型")
    private String tradeTypeStr;


    /**
     * 交易金额
     */
    @ExcelProperty("交易金额")
    private BigDecimal amount;

    @ExcelProperty("币种")
    private String currency;

    /**
     * 交易汇率
     */

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    /**
     * 笔数
     */
    @ExcelProperty("笔数")
    private int countAmount;

    /**
     * 平台奖励
     */
    @ExcelIgnore
    private BigDecimal platformAward;

    /**
     * 商户奖励
     */
    @ExcelProperty("商户奖励")
    private BigDecimal merAward;

    /**
     * 平台佣金
     */
    @ExcelIgnore
    private BigDecimal platformBrokerage;

    /**
     * 商户佣金
     */
    @ExcelProperty("商户佣金")
    private BigDecimal merBrokerage;

    /**
     * 平台应结算
     */
    @ExcelIgnore
    private BigDecimal platformShouldSettled;

    /**
     * 商户应结算
     */
    @ExcelProperty("商户应结算")
    private BigDecimal merShouldSettled;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 结算截图
     */
    private String settleUrl;

}