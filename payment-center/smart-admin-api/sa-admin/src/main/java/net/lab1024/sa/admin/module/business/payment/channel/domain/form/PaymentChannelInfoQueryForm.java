package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 渠道基本信息表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelInfoQueryForm extends PageParam{

    @Schema(description = "商户名称")
    private String merName;

    @Schema(description = "商户编码")
    private String merCode;

    @Schema(description = "入金开通标志")
    private String paymentFlag;

    @Schema(description = "出金开通标志")
    private String cashFlag;

    @Schema(description = "状态 0-禁用；1-启用；")
    private Integer status;

    @Schema(description = "接口类型 1-API方式，2-非API方式（通过【渠道管理平台】支持）")
    private Integer interfaceType;
}