package net.lab1024.sa.admin.module.business.skucfg.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品配置 更新表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */

@Data
public class SkuCfgUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品编号 不能为空")
    private String sku;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名称 不能为空")
    private String name;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "价格 不能为空")
    private BigDecimal price;

    @Schema(description = "手续费", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "手续费 不能为空")
    private BigDecimal charge;

    @Schema(description = "奖励", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "奖励 不能为空")
    private String award;

    @Schema(description = "收款卡id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收款卡id 不能为空")
    private Long collectionCardId;

}