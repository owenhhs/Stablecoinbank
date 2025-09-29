package net.lab1024.sa.admin.module.business.collectioncard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 收款银行卡表 实体类
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@Data
@TableName("t_collection_card")
public class CollectionCardEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 开户行
     */
    private String bankAddress;

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 开户人名称
     */
    private String accountName;

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