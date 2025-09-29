package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/19 17:35
 */
@Data
public class PaymentChannelInfoAddForm {

    @Schema(description = "父级部门ID")
    private Long parentDepartmentId;

    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String merName;

    /**
     * 商户编码
     */
    @Schema(description = "商户编码")
    private String merCode;

    /**
     * 通知手机号
     */
    @Schema(description = "通知手机号")
    private String phone;

    /**
     * 路由类型 1-百分比 2-轮询
     */
    @Schema(description = "路由类型")
    private Integer routeType;

    /**
     * 支付比例（百分比，路由使用）
     */
    @Schema(description = "支付比例（百分比，路由使用）")
    private Integer paymentScale;

    /**
     * 支付限额
     */
    @Schema(description = "支付限额")
    private BigDecimal paymentLimit;

    /**
     * 支付笔数限制
     */
    @Schema(description = "支付笔数限制")
    private Integer paymentCount;
    /**
     * 状态，0-禁用；1-启用；
     */
    @Schema(description = "状态，0-禁用；1-启用；")
    private Integer status;

    /**
     * 是否黑名单（1：黑名单，2：非黑名单）
     */
    @Schema(description = "是否黑名单（1：黑名单，2：非黑名单）")
    private Integer blackList;


    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;


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


}
