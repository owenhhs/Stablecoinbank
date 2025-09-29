package net.lab1024.sa.admin.module.business.settle.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户结算单表 实体类
 */

@Data
@TableName("t_payment_settle_parent")
public class PaymentSettleParentEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 结算状态 1:待结算，2:结算中，3:待确认，4:已结算
     */
    private int settleStatus;


    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 交易日期（年-月-日）
     */
    private String tradeDate;

    /**
     * 交易类型1：支付单，2兑付单
     */
    private int tradeType;


    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 笔数
     */
    private int countAmount;

    /**
     * 平台奖励
     */
    private BigDecimal platformAward;

    /**
     * 商户奖励
     */
    private BigDecimal merAward;

    /**
     * 平台佣金
     */
    private BigDecimal platformBrokerage;

    /**
     * 商户佣金
     */
    private BigDecimal merBrokerage;

    /**
     * 平台应结算
     */
    private BigDecimal platformShouldSettled;

    /**
     * 商户应结算
     */
    private BigDecimal merShouldSettled;

    /**
     * 结算截图
     */
    private String settleUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}