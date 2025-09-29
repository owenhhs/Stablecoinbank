package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDepositVO {

    /**
     * 收款人银行如：中要银行
     */
    private String bankInfo;

    /**
     * 开户行如：中国银行中关村支行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     *
     * 开户行人名称
     */
    private String username;

    /**
     * 收款人二维码码信息，如果是微信或者支付宝支付的话
     */
    private String payUrl;

    /**
     * 商户名称
     */
    private String merName;
}
