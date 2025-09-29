package net.lab1024.sa.admin.module.business.skucfg.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商品配置 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */

@Data
public class SkuCfgVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商品编号")
    private String sku;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "手续费")
    private BigDecimal charge;

    @Schema(description = "奖励")
    private String award;

    @Schema(description = "收款卡id")
    private Long collectionCardId;

    @Schema(description = "删除标识")
    private Integer deleteFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}