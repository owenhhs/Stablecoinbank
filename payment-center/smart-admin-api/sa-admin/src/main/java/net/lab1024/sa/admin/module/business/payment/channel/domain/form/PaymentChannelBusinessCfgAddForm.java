package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付渠道业务范围配置表 新建表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelBusinessCfgAddForm {

//    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "主键 不能为空")
//    private Long id;

    @Schema(description = "商户id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商户id 不能为空")
    private Long channelId;

    @Schema(description = "支付限额最小值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付限额最小值 不能为空")
    private BigDecimal amountMin;

    @Schema(description = "支付限额最大值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付限额最大值 不能为空")
    private BigDecimal amountMax;

    @Schema(description = "佣金比例，百分比", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "佣金比例，百分比 不能为空")
    private BigDecimal brokerage;

    @Schema(description = "单笔奖励", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单笔奖励 不能为空")
    private BigDecimal award;

//    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "创建时间 不能为空")
//    private LocalDateTime createTime;

//    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "更新时间 不能为空")
//    private LocalDateTime updateTime;

}