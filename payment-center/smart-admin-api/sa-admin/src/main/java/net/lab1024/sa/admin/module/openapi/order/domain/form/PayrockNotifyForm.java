package net.lab1024.sa.admin.module.openapi.order.domain.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/10/21 15:37
 */
@Data
public class PayrockNotifyForm {
    private String cartId;
    private BigDecimal orderAmount;
    private BigDecimal originalAmount;
    private String signature;
    private String orderTime;
    private String completedTime;
    private String paymentReference;
    private String currency;
    private String version;
    private String statusMessage;
    private String status;
}
