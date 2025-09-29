package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/07/25 22:50
 */
@Data
public class OrderPayInfoVO {
    private String orderNo;
    private String depositType;
    private String depositRemark;
    private String depositHolder;

    private BigDecimal amount;
    private String account;
    private String name;
    private String bank;

    private Long payTime;
}
