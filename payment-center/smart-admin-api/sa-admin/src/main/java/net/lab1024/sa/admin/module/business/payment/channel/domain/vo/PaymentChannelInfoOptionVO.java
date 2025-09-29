package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 渠道基本信息表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentChannelInfoOptionVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户名称")
    private String merName;

    @Schema(description = "商户编码")
    private String merCode;
}