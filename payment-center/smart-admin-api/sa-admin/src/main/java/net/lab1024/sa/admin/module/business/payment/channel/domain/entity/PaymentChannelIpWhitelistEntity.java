package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 渠道基本信息表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@TableName("t_payment_channel_ip_whitelist")
public class PaymentChannelIpWhitelistEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
     */
    private Long channelId;

    /**
     * ip
     */
    private String ip;

    /**
     * 删除状态，0-未删除；1-已删除；
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}