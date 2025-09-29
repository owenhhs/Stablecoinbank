package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 结算单表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@TableName("t_settle_bill")
public class SettleBillEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道商户Id
     */
    private Long channelId;

    /**
     * 业务范围Id
     */
    private Long businessId;

    /**
     * 交易日期s
     */
    private String tradeTime;

    /**
     * 交易类型，payment-支付单；cash-兑付单；
     */
    private String tradeType;

    /**
     * 业务范围
     */
    private String tradeLimit;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;

    /**
     * 订单笔数
     */
    private Integer orderCount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 佣金
     */
    private BigDecimal brokerage;

    /**
     * 奖励
     */
    private BigDecimal award;

    /**
     * 结算金额
     */
    private BigDecimal settleAmount;

    /**
     * 结算状态，0 待结算  1 结算中 2 已结算
     */
    private Integer settleStatus;


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