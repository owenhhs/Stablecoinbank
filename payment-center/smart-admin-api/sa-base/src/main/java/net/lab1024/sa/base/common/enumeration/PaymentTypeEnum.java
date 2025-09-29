package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author sunyu
 * @Date 2024/07/25 21:47:13
 */

@Getter
@AllArgsConstructor
public enum PaymentTypeEnum implements BaseEnum {

    /**
     * 支付
     */
    payment("payment", "支付"),

    /**
     * 兑付
     */
    cash("cash", "兑付"),
    ;
    private final String value;

    private final String desc;

}
