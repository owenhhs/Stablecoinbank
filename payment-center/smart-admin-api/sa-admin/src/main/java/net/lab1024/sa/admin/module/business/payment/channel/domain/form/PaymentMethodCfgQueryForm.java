package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 支付渠道支付方式配置表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentMethodCfgQueryForm extends PageParam{

    @Schema(description = "渠道商户id")
    private Long channelId;

    @Schema(description = "支付方式类型，payment-支付方式；cash-兑付方式；")
    private String paymentType;

    @Schema(description = "状态，0-禁用；1-启用；")
    private Integer status;

}