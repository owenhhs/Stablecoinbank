package net.lab1024.sa.admin.module.business.collectioncard.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收款银行卡表 新建表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Data
public class CollectionCardAddForm {

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "银行名称 不能为空")
    private String bankName;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开户行 不能为空")
    private String bankAddress;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "银行卡号 不能为空")
    private String cardNo;

    @Schema(description = "开户人名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开户人名称 不能为空")
    private String accountName;

}