package net.lab1024.sa.admin.module.business.settle.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 结算设置汇率
 *
 */

@Data
public class SettleSetExchangeRateForm {

    @Schema(description = "结算id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long settleId;

    @Schema(description = "汇率", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal exchangeRate;


    @Schema(description = "结算凭证", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> fileIds;

}