package net.lab1024.sa.admin.module.business.orderinfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import net.lab1024.sa.base.common.enumeration.OrderStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import java.util.List;

/**
 * 订单信息表 分页查询表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Data
public class OrderInfoQueryForm extends PageParam{
    @Schema(description = "订单编号")
    private String orderNo;
    @Schema(description = "收款卡号")
    private String collectionCardNo;
    @Schema(description = "付款人")
    private String depositHolder;
    @SchemaEnum(value = OrderStatusEnum.class, desc = "订单状态 1 待确认 2 已确认 3 挂起", required = false)
    private Integer status;
    @Schema(description = "开始时间")
    private String startDate;
    @Schema(description = "结束时间")
    private String endDate;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "部门id")
    private Long departmentId;

    private List<Long> departmentIds;

    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;

    private String depositHolderHash;
}