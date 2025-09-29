package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 结算单订单关联表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */

@Data
@TableName("t_settle_order")
public class SettleOrderEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道商户Id
     */
    private Long channelId;

    /**
     * 结算单id
     */
    private Long settleBillId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单类型，payment-支付订单；cash-兑付订单；
     */
    private String orderType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}