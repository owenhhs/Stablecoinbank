package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 支付订单信息表 分页查询表单
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentOrderInfoQueryForm extends PageParam {

    @Schema(description = "平台ID")
    private Long platformId;

    @Schema(description = "商户编码")
    private String merNo;

    @Schema(description = "支付状态")
    private Integer payStatus;

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "付款方式")
    private String paymentMethod;

    @Schema(description = "币种")
    private String currency;

    private String keywordHash;

}