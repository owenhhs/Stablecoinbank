package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 结算单表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class SettleBillExportQueryForm {

    @Schema(description = "商户编码")
    private String merNo;

    @Schema(description = "结算状态")
    private Integer settleStatus;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "币种")
    private String currency;

    private Long pageNum;
    private Long pageSize;
}