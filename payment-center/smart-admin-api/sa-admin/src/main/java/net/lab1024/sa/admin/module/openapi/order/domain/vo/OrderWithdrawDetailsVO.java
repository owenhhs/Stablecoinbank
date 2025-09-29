package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderWithdrawDetailsVO {
    private String orderNo;
    private BigDecimal amount;
    private String ext;
    private Integer status;
    private LocalDateTime reqTime;
    private LocalDateTime endTime;
    private List<WithdrawDetail> list;

    @Data
    public static class WithdrawDetail {
        private String chnlId;
        private String chnlName;
        private BigDecimal amount;
    }

}
