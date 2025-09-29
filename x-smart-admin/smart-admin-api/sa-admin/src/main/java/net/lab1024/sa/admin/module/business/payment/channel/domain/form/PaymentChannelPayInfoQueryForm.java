package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.util.List;

/**
 * 订单信息表 新建表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentChannelPayInfoQueryForm extends PageParam {

    @Schema(description = "部门id(商户id)")
    private Long departmentId;

    @Schema(description = "支付类型")
    private String type;

    @Schema(description = "收款人姓名")
    private String username;

    @Schema(description = "收款账号/银行卡号")
    private String bankNo;

    @Schema(description = "状态")
    private Integer status;

    private List<Long> departmentIds;

    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;

}