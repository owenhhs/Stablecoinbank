package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单处理状态
 * @author 孙宇
 * @date 2024/07/24 22:29
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum implements BaseEnum {
    /**
     * 待处理
     */
    WAITING(1, "待处理"),

    /**
     * 已确认
     */
    CONFIRM(2, "已确认"),
    /**
     * 挂起
     */
    HANG_UP(3, "已取消"),

    /**
     * 已过期
     */
    EXPIRED(4, "已过期"),
    ;
    private final Integer value;

    private final String desc;
}
