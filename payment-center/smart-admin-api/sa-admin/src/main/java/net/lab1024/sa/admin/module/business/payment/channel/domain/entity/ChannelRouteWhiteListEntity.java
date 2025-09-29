package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 支付渠道路由白名单 实体类
 *
 * @Author Sunny
 * @Date 2024-10-16 15:02:53
 * @Copyright Sunny
 */

@Data
@TableName("t_channel_route_white_list")
public class ChannelRouteWhiteListEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道id
     */
    private Long channelId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 启用状态，0-禁用；1-启用；
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