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
public enum WithdrawSubOrderStatusEnum implements BaseEnum {
    /**
     * 处理中
     */
    PROCESSING(1, "处理中"),

    /**
     * 处理成功
     */
    SUCCESSED(2, "处理成功"),

    /**
     * 处理失败
     */
    FAILED(3, "处理失败"),
    ;
    private final Integer value;

    private final String desc;
}
