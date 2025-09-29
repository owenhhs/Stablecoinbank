package net.lab1024.sa.admin.module.business.payment.channel.domain.dto;

import lombok.Data;

/**
 * 路由dto
 */

@Data
public class PaymentChannelInfoRouteDTO {

    /**
     * 部门id
     */
    private Long departmentId;

    private Long paymentChannelBusinessId;

    private Long paymentChannelPayInfoId;

    private String merName;

    /**
     * 路由类型 1-百分比 2-轮询
     */
    private Integer routeType;

    /**
     * 开户行人名称
     */
    private String username;

    /**
     * 银行：如工商银行
     */
    private String bankInfo;

    /**
     * 开户行：工商银行，张家口分行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 收款人二维码码信息，如果是微信或者支付宝支付的话
     */
    private String payUrl;

    private String currency;

    private String country;
}