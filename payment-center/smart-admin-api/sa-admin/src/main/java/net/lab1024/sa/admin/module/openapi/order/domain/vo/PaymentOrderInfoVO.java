package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/05 00:29
 */
@Data
public class PaymentOrderInfoVO {

    /**
     * 支付状态 1 待付款 2 已付款 3 已取消 4 已过期
     */
    private Integer paymentStatus;

    /**
     * 支付方式，bank、Alipay、WeChat
     */
    private String paymentMethod;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 服务器时间
     */
    private Long serverTime;

    /**
     * 跳转地址
     */
    private String jumpUrl;

    /**
     * 订单信息
     */
    private OrderInfo orderInfo;

    /**
     * 收款卡信息
     */
    private BankInfo bankInfo;

    /**
     * 二维码信息
     */
    private QrCodeInfo qrCodeInfo;


    @Data
    public static class OrderInfo {

        /**
         * 支付金额
         */
        private BigDecimal amount;

        /**
         * 付款人
         */
        private String payer;
    }


    @Data
    public static class BankInfo {
        /**
         * 银行账户
         */
        private String bankAccount;
        /**
         * 银行卡号
         */
        private String bankCardNo;
        /**
         * 银行名称
         */
        private String bankName;
        /**
         * 开户支行
         */
        private String bankBranch;

        public BankInfo() {
            this.bankAccount = "";
            this.bankCardNo = "";
            this.bankName = "";
            this.bankBranch = "";
        }
    }

    @Data
    public static class QrCodeInfo {
        /**
         * 收款账户
         */
        private String account;
        /**
         * 收款人
         */
        private String username;
        /**
         * 二维码
         */
        private String qrcode;

        /**
         * 二维码内容
         */
        private String qrcodeContent;

        public QrCodeInfo() {
            this.account = "";
            this.username = "";
            this.qrcode = "";
            this.qrcodeContent = "";
        }
    }


}
