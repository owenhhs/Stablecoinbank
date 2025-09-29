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
public enum LetonggouPaymentFlagEnum implements BaseEnum {
    /**
     * 阿里收款码
     */
    Alipay("Alipay", "阿里收款码", "alipay"),

    WeChat("WeChat", "微信收款码", "wxpay"),

    ;
    private final String value;

    private final String desc;

    private final String paymentFlag;

    public static LetonggouPaymentFlagEnum getByFlag(String flag) {
        if (LetonggouPaymentFlagEnum.Alipay.getPaymentFlag().equals(flag)) {
            return LetonggouPaymentFlagEnum.Alipay;
        } else if (LetonggouPaymentFlagEnum.WeChat.getPaymentFlag().equals(flag)) {
            return LetonggouPaymentFlagEnum.WeChat;
        }
        return LetonggouPaymentFlagEnum.Alipay;
    }
}
