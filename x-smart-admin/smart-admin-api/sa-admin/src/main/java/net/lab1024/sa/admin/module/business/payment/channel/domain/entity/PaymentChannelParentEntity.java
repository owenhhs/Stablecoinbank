package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 主渠道表 实体类
 */

@Data
@TableName("t_payment_channel_parent")
public class PaymentChannelParentEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 商户ak
     */
    private String merAk;

    /**
     * 商户秘钥
     */
    private String merSk;

    /**
     * 是否自营，0-否；1-是；
     */
    private Integer isOwned;

    /**
     * 状态，0-禁用；1-启用；
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}