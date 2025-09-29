package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;

/**
 * @author 孙宇
 * @date 2024/09/06 01:55
 */
@Data
public class OrderWithdrawDetailVO {
    /**
     * 订单状态
     */
    private WithdrawSubOrderStatusEnum status;

    private String endtime;
}
