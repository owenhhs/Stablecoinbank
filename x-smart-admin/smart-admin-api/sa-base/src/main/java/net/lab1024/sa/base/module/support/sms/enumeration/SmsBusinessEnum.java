package net.lab1024.sa.base.module.support.sms.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * @author 孙宇
 * @date 2024/11/08 00:19
 */
@Getter
@AllArgsConstructor
public enum SmsBusinessEnum implements BaseEnum {

    ORDER_NOTIFY("order_notify", "订单通知"),
    ORDER_FAIL("order_fail", "下单失败"),
    ;


    private final String value;
    private final String desc;

}
