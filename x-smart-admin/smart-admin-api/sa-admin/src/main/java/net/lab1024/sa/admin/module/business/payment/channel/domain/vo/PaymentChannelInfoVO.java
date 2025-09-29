package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentChannelInfoVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 父级部门ID
     */
    private Long parentDepartmentId;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 通知手机号
     */
    private String phone;

    /**
     * 路由类型 1-百分比 2-轮询
     */
    private Integer routeType;

    /**
     * 支付比例（百分比，路由使用）
     */
    private Integer paymentScale;

    /**
     * 支付限额
     */
    private BigDecimal paymentLimit;

    /**
     * 支付笔数限制
     */
    private Integer paymentCount;
    /**
     * 状态，0-禁用；1-启用；
     */
    private Integer status;

    /**
     * 支付限额最小值
     */
    private BigDecimal amountMin;

    /**
     * 支付限额最大值
     */
    private BigDecimal amountMax;

    /**
     * 平台佣金比例，百分比
     */
    private BigDecimal platformBrokerage;

    /**
     * 商户佣金比例，百分比
     */
    private BigDecimal merBrokerage;

    /**
     * 平台奖励
     */
    private BigDecimal platformAward;

    /**
     * 商户奖励
     */
    private BigDecimal merAward;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否黑名单（1：黑名单，2：非黑名单）
     */
    private Integer blackList;
}
