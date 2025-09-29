package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付订单信息表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class OrderCountVO {

    @Schema(description = "渠道商户id")
    private Long channelId;

    @Schema(description = "存款金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单数量")
    private Integer orderCount;
}