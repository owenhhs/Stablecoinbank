package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/19 16:57
 */
@Data
public class PaymentChannelBusinessVO {
    private Long id;

    private BigDecimal amountMin;

    private BigDecimal amountMax;

    private BigDecimal platformBrokerage;
    private BigDecimal platformAward;

    private BigDecimal merBrokerage;
    private BigDecimal merAward;
}
