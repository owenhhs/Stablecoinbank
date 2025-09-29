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
     * 支付宝
     */
    ALIPAY("Alipay", "支付宝"),

    /**
     * 二维码
     */
    WECHAT("WeChat", "二维码"),
    ;
    private final String value;

    private final String desc;

}
