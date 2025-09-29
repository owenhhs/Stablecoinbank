package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

/**
 * @author 孙宇
 * @date 2024/09/06 01:55
 */
@Data
public class OrderDetailVO {
    /**
     * 订单状态 1 待确认 2 已确认 3 挂起
     */
    private Integer status;

    private String endtime;
}
