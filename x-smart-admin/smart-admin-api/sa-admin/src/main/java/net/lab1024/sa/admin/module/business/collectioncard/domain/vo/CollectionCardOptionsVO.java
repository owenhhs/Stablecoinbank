package net.lab1024.sa.admin.module.business.collectioncard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收款银行卡表 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Data
public class CollectionCardOptionsVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行卡号")
    private String cardNo;

    @Schema(description = "开户人名称")
    private String accountName;
}