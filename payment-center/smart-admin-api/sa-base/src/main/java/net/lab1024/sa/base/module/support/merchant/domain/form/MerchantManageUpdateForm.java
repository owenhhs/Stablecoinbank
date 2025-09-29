package net.lab1024.sa.base.module.support.merchant.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商户表 更新表单
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Data
public class MerchantManageUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "商户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商户名称 不能为空")
    private String merName;

    @Schema(description = "商户状态，0-禁用；1-启用；", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商户状态，0-禁用；1-启用； 不能为空")
    private Integer status;

}