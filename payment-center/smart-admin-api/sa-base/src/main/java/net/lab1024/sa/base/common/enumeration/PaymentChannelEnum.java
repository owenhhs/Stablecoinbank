package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author sunyu
 * @Date 2024/07/25 21:47:13
 */

@Getter
@AllArgsConstructor
public enum PaymentChannelEnum implements BaseEnum {

    /**
     * 微信
     */
    Union("Union", "银联"),
    /**
     * 支付宝
     */
    Alipay("Alipay", "支付宝"),

    /**
     * 微信
     */
    WeChat("WeChat", "微信"),

    /**
     * THB Thailand 二维码
     */
    QR_CODE("QR_CODE", ""),
    TRUE_MONEY("TRUE_MONEY", ""),

    /**
     * VND Vietnam 二维码
     */
    MOMOPAY("MOMOPAY", ""),
    ZALO("ZALO", ""),
    VIETTEL_PAY("VIETTEL_PAY", ""),
    P2C("P2C", ""),

    /**
     * IDR Indonesia 二维码
     */
    QRIS("QRIS", ""),

    ;
    private final String value;

    private final String desc;

}
