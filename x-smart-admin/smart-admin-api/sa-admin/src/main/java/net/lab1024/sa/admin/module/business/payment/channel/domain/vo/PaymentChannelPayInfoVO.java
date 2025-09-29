package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 孙宇
 * @date 2024/09/19 16:59
 */
@Data
public class PaymentChannelPayInfoVO {
    private Long id;

    private Long departmentId;

    private String merName;

    private String type;

    private String username;

    private String bankInfo;
    private String bankName;
    private String bankNo;
    private String currency;
    private String country;

    private Integer blackList;

    private Integer paymentScale;

    private BigDecimal paymentLimit;

    private Integer paymentCount;

    private String payUrl;
    private String workTime;
    private Integer xinjiang;

    private Integer status;

    private BigDecimal amountMin;

    private BigDecimal amountMax;

    private LocalDateTime updateTime;
}
