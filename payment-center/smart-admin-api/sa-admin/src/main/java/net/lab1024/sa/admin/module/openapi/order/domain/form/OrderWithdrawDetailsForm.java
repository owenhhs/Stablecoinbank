package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.BaseRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OrderWithdrawDetailsForm implements BaseRequest {
    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单号 不能为空")
    @Size(min = 1, max = 30, message = "订单号长度不能超过30")
    private String orderNo;
}
