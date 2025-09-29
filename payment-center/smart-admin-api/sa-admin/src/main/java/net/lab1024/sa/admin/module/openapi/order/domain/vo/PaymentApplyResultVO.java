package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

/**
 * @author 孙宇
 * @date 2024/09/04 23:43
 */
@Data
public class PaymentApplyResultVO {
    private String thirdpartyOrderNo;
    private String subMerName;

    private String paymentUrl;

    /**
     * 是否新收银台页面
     */
    private boolean newCashier;
}
