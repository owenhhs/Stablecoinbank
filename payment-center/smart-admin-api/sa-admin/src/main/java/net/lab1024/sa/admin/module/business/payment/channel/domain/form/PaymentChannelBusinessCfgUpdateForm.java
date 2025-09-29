package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付渠道业务范围配置表 更新表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelBusinessCfgUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "支付方式类型，payment-支付方式；cash-兑付方式；", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付方式类型，payment-支付方式；cash-兑付方式； 不能为空")
    private String paymentType;

    private BigDecimal amountMax;
    private BigDecimal amountMin;
    private BigDecimal award;
    private BigDecimal brokerage;
    private String country;
    private String currency;
}