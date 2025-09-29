package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 结算单计算任务表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-10 14:51:11
 * @Copyright Sunny
 */

@Data
@TableName("t_settle_calc_task")
public class SettleCalcTaskEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 交易日期
     */
    private String tradeTime;

    /**
     * 交易类型，payment-支付单；cash-兑付单；
     */
    private String tradeType;

    /**
     * 任务开始时间
     */
    private Long startTime;

    /**
     * 任务结束时间
     */
    private Long endTime;

    /**
     * 任务执行状态，0-任务待执行；1-任务进行中；2-任务已完成；3-任务重跑；4-任务异常;
     */
    private Integer taskStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}