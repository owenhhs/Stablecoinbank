package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.BaseRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author 孙宇
 * @date 2024/07/25 22:49
 */
@Data
public class OrderPayInfoForm implements BaseRequest {

    @Schema(description = "临时token", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "临时token 不能为空")
    @Size(min = 1, max = 32)
    private String token;
}
