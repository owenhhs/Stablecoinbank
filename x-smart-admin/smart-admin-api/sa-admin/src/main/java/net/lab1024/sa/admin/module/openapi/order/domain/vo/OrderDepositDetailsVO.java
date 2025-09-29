package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 孙宇
 * @date 2024/07/25 22:50
 */
@Data
public class OrderDepositDetailsVO {
    private String orderNo;
    private String depositType;
    private String depositRemark;
    private String depositHolder;

    private BigDecimal amount;
    private String currency;
    private String country;
    private String account;
    private String name;
    private String bank;

    private Integer status;
    private Integer payStatus;
    private LocalDateTime time;
    private LocalDateTime updateTime;
}
