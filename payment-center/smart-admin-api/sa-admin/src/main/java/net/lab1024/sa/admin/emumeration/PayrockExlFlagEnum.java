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
public enum PayrockExlFlagEnum implements BaseEnum {
    /**
     * 支付宝二维码
     */
    Alipay("Alipay", "支付宝二维码", "Alipay"),


    ;
    private final String value;

    private final String desc;

    private final String paymentFlag;
}
