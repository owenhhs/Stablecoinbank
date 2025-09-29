package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/09/19 17:35
 */
@Data
public class PaymentChannelParentUpdateForm {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;
    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String merName;

    /**
     * 状态，0-禁用；1-启用；
     */
    @Schema(description = "状态，0-禁用；1-启用；")
    private Integer status;

}
