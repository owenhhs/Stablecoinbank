package net.lab1024.sa.base.module.support.merchant.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.common.domain.RequestMerchant;

/**
 * 商户表 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Data
public class MerchantManageVO implements RequestMerchant {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户号AK")
    private String merNo;

    @Schema(description = "商户名称")
    private String merName;

    @Schema(description = "商户秘钥SK")
    private String secretKey;

    @Schema(description = "rsa 解密公钥")
    private String rsaPublicKey;

    @Schema(description = "商户状态，0-禁用；1-启用；")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}