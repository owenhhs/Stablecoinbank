package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/07/25 22:50
 */
@Data
public class OrderDepositVO {
    private String payUrl;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 服务器时间
     */
    private Long serverTime;

    /**
     * 订单信息
     */
    private PaymentOrderInfoVO.OrderInfo orderInfo;

    /**
     * 收款卡信息
     */
    private PaymentOrderInfoVO.BankInfo bankInfo;

    /**
     * 二维码信息
     */
    private PaymentOrderInfoVO.QrCodeInfo qrCodeInfo;
}
