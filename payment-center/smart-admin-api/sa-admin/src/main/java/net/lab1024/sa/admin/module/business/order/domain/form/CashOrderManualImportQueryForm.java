package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 兑付订单信人工导入表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class CashOrderManualImportQueryForm extends PageParam{
    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "交易状态")
    private Integer status;

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "关键字")
    private String keyword;

}