package net.lab1024.sa.admin.module.business.collectioncard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 收款银行卡表 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Data
public class CollectionCardVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "开户行")
    private String bankAddress;

    @Schema(description = "银行卡号")
    private String cardNo;

    @Schema(description = "开户人名称")
    private String accountName;

    @Schema(description = "删除标识")
    private Integer deleteFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}