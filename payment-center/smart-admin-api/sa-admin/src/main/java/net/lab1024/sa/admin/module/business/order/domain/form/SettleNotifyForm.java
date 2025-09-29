package net.lab1024.sa.admin.module.business.order.domain.form;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孙宇
 * @date 2025/03/20 21:40
 */
@Data
public class SettleNotifyForm {
    private String merCode;
    private String tradeDate;
    private String tradeType;
    private BigDecimal tradeAmount;
    private Integer orderCount;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal brokerage;
    private BigDecimal award;
    private BigDecimal settleAmount;
    private List<String> settleUrls;
}
