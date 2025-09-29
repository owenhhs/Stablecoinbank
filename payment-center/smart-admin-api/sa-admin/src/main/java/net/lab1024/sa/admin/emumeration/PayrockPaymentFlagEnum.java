package net.lab1024.sa.admin.emumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * @author 孙宇
 * @date 2024/09/04 22:11
 */
@Getter
@AllArgsConstructor
public enum PayrockPaymentFlagEnum implements BaseEnum {
    /**
     * 阿里收款码
     */
    Alipay("Alipay", "阿里收款码", "ALIPAY"),

    /**
     * THB Thailand 二维码
     */
    QR_CODE("QR_CODE", "", "QR_CODE"),
    TRUE_MONEY("TRUE_MONEY", "", "TRUE_MONEY"),

    /**
     * VND Vietnam 二维码
     */
    MOMOPAY("MOMOPAY", "", "MOMOPAY"),
    ZALO("ZALO", "", "ZALO"),
    VIETTEL_PAY("VIETTEL_PAY", "", "VIETTEL_PAY"),
    P2C("P2C", "", "P2C"),

    /**
     * IDR Indonesia 二维码
     */
    QRIS("QRIS", "", "QRIS"),

    ;
    private final String value;

    private final String desc;

    private final String paymentFlag;
}
