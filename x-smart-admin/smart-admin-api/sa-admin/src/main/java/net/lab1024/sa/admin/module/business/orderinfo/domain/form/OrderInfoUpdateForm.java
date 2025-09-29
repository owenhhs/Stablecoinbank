package net.lab1024.sa.admin.module.business.orderinfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import net.lab1024.sa.base.common.enumeration.OrderStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

/**
 * 订单信息表 更新表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Data
public class OrderInfoUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @SchemaEnum(value = OrderStatusEnum.class, desc = "订单状态 1 待确认 2 已确认 3 挂起")
    @NotNull(message = "订单状态 1 待确认 2 已确认 3 挂起 不能为空")
    private Integer status;

    @Schema(description = "退款原因")
    private String refundReason;

}