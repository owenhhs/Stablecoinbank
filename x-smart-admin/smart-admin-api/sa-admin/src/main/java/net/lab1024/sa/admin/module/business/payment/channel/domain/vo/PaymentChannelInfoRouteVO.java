package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;

/**
 * 渠道基本信息表 vo类
 */

@Data
public class PaymentChannelInfoRouteVO {

    /**
     * 部门id
     */
    private Long departmentId;

    private Long id;

    /**
     * 路由类型 1-百分比 2-轮询
     */
    private Integer routeType;
    /**
     * 支付比例（百分比，路由使用）
     */
    private Integer paymentScale;

    private String merName;

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

    private String payUrl;

    private Long paymentChannelBusinessId;
    private Long paymentChannelPayInfoId;

}