package net.lab1024.sa.admin.module.business.settle.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import net.lab1024.sa.base.common.enumeration.OrderStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class SettleDetailQueryForm extends PageParam{

    @Schema(description = "结算主键id")
    private String settleId;
    @Schema(description = "结算开始时间")
    private String settleStartDate;
    @Schema(description = "结算结束时间")
    private String settleEndDate;

    private List<Long> settleIds;
}