package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;


@Data
public class SettleQueryForm extends PageParam{
    @Schema(description = "结算状态 1:待结算，2:结算中，3:待确认，4:已结算")
    private int settleStatus;
    @Schema(description = "结算开始时间")
    private String settleStartDate;
    @Schema(description = "结算结束时间")
    private String settleEndDate;
    @Schema(description = "币种")
    private String currency;
}