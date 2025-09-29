package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 渠道基本信息表 更新表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelInfoUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "商户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商户名称 不能为空")
    private String merName;

    @Schema(description = "商户号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "商户号 不能为空")
    private String merAk;

    @Schema(description = "商户秘钥", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "商户秘钥 不能为空")
    private String merSk;

    @Schema(description = "支付开通标志 1-开通 0-未开通", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付开通标志 1-开通 0-未开通 不能为空")
    private Integer paymentFlag;

    @Schema(description = "支付比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付比例 不能为空")
    private Integer paymentScale;

    @Schema(description = "支付限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付限额 不能为空")
    private BigDecimal paymentLimit;

    @Schema(description = "支付笔数限制", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付笔数限制 不能为空")
    private Integer paymentCount;

    @Schema(description = "兑付开通标志 1-开通 0-未开通", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "兑付开通标志 1-开通 0-未开通 不能为空")
    private Integer cashFlag;

    @Schema(description = "兑付比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "兑付比例 不能为空")
    private Integer cashScale;

    @Schema(description = "兑付限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "兑付限额 不能为空")
    private BigDecimal cashLimit;

    @Schema(description = "兑付笔数限制", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "兑付笔数限制 不能为空")
    private Integer cashCount;

    @Schema(description = "工作时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接口类型 不能为空")
    private String workTime;

    @Schema(description = "接口类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接口类型 不能为空")
    private Integer interfaceType;

    @Schema(description = "是否支持新疆用户 0-不支持 1-支持", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否支持新疆用户 不能为空")
    private Integer xjFlag;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态 不能为空")
    private Integer status;

}