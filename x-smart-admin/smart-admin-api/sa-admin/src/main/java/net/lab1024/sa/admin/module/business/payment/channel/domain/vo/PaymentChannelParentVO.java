package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentChannelParentVO {
    /**
     * 主键
     */
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
