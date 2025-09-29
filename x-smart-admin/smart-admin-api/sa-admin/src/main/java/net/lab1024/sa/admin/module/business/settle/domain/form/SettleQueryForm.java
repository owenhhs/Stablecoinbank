package net.lab1024.sa.admin.module.business.settle.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

import java.util.List;


@Data
public class SettleQueryForm extends PageParam{
    @Schema(description = "部门id(商户id)")
    private Long departmentId;
    @Schema(description = "结算状态 1:待结算，2:结算中，3:待确认，4:已结算")
    private int settleStatus;
    @Schema(description = "结算开始时间")
    private String settleStartDate;
    @Schema(description = "结算结束时间")
    private String settleEndDate;
    @Schema(description = "币种")
    private String currency;

    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;

    private List<Long> departmentIds;

}