package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 支付渠道业务范围配置表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelBusinessCfgVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户id")
    private Long channelId;

    @Schema(description = "支付方式类型，payment-支付方式；cash-兑付方式；")
    private String paymentType;

    @Schema(description = "支付限额最小值")
    private BigDecimal amountMin;

    @Schema(description = "支付限额最大值")
    private BigDecimal amountMax;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "佣金比例，百分比")
    private BigDecimal brokerage;

    @Schema(description = "单笔奖励")
    private BigDecimal award;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}