package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 结算单订单关联表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */

@Data
public class SettleOrderQueryForm extends PageParam{

    @Schema(description = "结算单ID")
    private Long settleId;
}