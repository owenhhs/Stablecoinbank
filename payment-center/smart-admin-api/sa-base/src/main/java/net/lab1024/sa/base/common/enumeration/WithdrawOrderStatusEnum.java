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
public enum WithdrawOrderStatusEnum implements BaseEnum {

    init(0, "初始化"),

    Pending(1, "待处理"),

    Processing(2, "处理中"),

    Successed(3, "处理成功")
    ;
    private final Integer value;

    private final String desc;
}
