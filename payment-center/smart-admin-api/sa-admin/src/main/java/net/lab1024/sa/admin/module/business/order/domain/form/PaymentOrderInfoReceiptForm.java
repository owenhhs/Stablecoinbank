package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支付订单补充回单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class PaymentOrderInfoReceiptForm {

    @Schema(description = "订单主键ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "文件id")
    private Long fileId;

}