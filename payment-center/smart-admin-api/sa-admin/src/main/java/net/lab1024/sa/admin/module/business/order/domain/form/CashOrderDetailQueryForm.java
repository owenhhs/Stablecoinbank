package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.enumeration.WithdrawSubOrderStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import javax.validation.constraints.NotNull;

/**
 * 兑付订单信息表 分页查询表单
 *
 * @Author bradydreamer
 * @Date 2025-01-22 22:12:31
 * @Copyright 2025
 */

@Data
public class CashOrderDetailQueryForm {

    @Schema(description = "订单编号")
    @NotNull(message = "订单编号不能为空")
    private String orderNo;

    @SchemaEnum(value = WithdrawSubOrderStatusEnum.class, desc = "交易状态")
    private Integer status;

}