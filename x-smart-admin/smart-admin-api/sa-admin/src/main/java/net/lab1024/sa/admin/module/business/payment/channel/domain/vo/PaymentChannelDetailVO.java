package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孙宇
 * @date 2024/09/19 16:54
 */
@Data
public class PaymentChannelDetailVO {

    private Long id;
    /**
     * 部门id
     */
    private Long departmentId;

    private String merName;

    private String phone;

    private Integer routeType;

    private Integer paymentScale;

    private BigDecimal paymentLimit;

    private Integer paymentCount;

    private Integer blackList;

    private Integer status;

    private List<PaymentChannelBusinessVO> businessList;

}
