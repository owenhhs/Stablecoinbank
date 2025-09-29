package net.lab1024.sa.base.module.support.merchant.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.RequestMerchant;

import java.time.LocalDateTime;

/**
 * 商户表 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Data
public class MerchantOptionVO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户号AK")
    private String merNo;

    @Schema(description = "商户名称")
    private String merName;
}