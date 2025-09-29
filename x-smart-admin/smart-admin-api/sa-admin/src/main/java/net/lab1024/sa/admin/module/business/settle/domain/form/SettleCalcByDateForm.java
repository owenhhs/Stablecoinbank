package net.lab1024.sa.admin.module.business.settle.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 计算结算单
 *
 */

@Data
public class SettleCalcByDateForm {
    @Schema(description = "结算日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private String settleDate;

}