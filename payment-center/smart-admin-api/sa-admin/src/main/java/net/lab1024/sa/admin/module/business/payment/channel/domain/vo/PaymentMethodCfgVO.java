package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 支付渠道支付方式配置表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentMethodCfgVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户id")
    private Long channelId;

    @Schema(description = "支付方式类型，payment-支付方式；cash-兑付方式；")
    private String paymentType;

    @Schema(description = "支付方式，bank-银行卡；Alipay-支付宝;")
    private String paymentMethod;

    @Schema(description = "状态，0-禁用；1-启用；")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}