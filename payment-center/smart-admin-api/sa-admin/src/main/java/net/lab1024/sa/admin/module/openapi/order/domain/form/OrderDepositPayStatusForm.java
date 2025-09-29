package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.BaseRequest;
import net.lab1024.sa.base.common.enumeration.OrderPayStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import javax.validation.constraints.NotBlank;

/**
 * @author 孙宇
 * @date 2024/07/25 22:49
 */
@Data
public class OrderDepositPayStatusForm implements BaseRequest {

    @Schema(description = "临时token", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "临时token 不能为空")
    private String token;

    @SchemaEnum(value = OrderPayStatusEnum.class, desc = "付款状态 1 待付款 2 已付款 3 已取消")
    private Integer payStatus;
}
