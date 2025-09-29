package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单支付状态
 * @author 孙宇
 * @date 2024/07/24 22:29
 */
@Getter
@AllArgsConstructor
public enum OrderPayStatusEnum implements BaseEnum {
    /**
     * 待付款
     */
    WAITING(1, "待付款"),

    /**
     * 已付款
     */
    SUCCESS(2, "已付款"),
    /**
     * 已取消
     */
    CANCELED(3, "已取消"),

    /**
     * 已过期
     */
    EXPIRED(4, "已过期"),
    ;
    private final Integer value;

    private final String desc;
}
