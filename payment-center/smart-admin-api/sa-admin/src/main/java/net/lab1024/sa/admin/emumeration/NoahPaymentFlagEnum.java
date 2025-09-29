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
public enum NoahPaymentFlagEnum implements BaseEnum {
    /**
     * 阿里收款码
     */
    Alipay("Alipay", "阿里收款码", "alipay_qr"),

    bank("bank", "银联", "union"),

    ;
    private final String value;

    private final String desc;

    private final String paymentFlag;
}
