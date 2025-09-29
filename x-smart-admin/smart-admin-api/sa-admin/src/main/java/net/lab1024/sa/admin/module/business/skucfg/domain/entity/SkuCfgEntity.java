package net.lab1024.sa.admin.module.business.skucfg.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商品配置 实体类
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */

@Data
@TableName("t_sku_cfg")
public class SkuCfgEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品编号
     */
    private String sku;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 手续费
     */
    private BigDecimal charge;

    /**
     * 奖励
     */
    private String award;

    /**
     * 收款卡id
     */
    private Long collectionCardId;

    /**
     * 删除标识
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}