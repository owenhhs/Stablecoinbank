package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

import java.time.LocalDateTime;

/**
 * 支付用户表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@EncryptedTable
@TableName("t_payment_user")
public class PaymentUserEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外部用户ID（交易所）
     */
    private String extUserId;

    /**
     * 用户名
     */
    @EncryptedColumn
    private String username;

    /**
     * 用户名hash
     */
    private String usernameHash;

    /**
     * 是否新疆用户，0-不是；1-是；
     */
    private Integer isXj;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}