package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * @author 孙宇
 * @date 2025/04/14 21:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentUserQueryForm extends PageParam {
    private Long userId;
    private String userName;
    private Integer isXj;
    private Integer black;
}

