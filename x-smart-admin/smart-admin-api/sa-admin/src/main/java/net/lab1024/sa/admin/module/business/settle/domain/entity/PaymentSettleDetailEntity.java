package net.lab1024.sa.admin.module.business.settle.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 结算表明细表 实体类
 */

@Data
@TableName("t_payment_settle_detail")
public class PaymentSettleDetailEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 交易日期
     */
    private LocalDateTime tradeDate;

    private Long paymentChannelBusinessId;


    /**
     * 结清表id
     */
    private Long settleId;


    /**
     * 订单号
     */
    private String  orderNo;


    /**
     * 业务范围（500-1000）
     */
    private String businessScope;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 付款方式 银行卡，支付宝，微信
     */
    private String paymentMethod;

    /**
     * 存款人
     */
    private String depositHolder;

    /**
     * 存款账号
     */
    private String bankAccount;

    /**
     * 收款人
     */
    private String collectionHolder;

    /**
     * 收款账号
     */
    private String collectionAccount;

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
     * 结算状态 1:待结算，2:结算中，3:待确认，4:已结算
     */
    private int  settleStatus;

    /**
     * 订单状态 1 待确认 2 已确认 3 挂起
     */
    private int status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}