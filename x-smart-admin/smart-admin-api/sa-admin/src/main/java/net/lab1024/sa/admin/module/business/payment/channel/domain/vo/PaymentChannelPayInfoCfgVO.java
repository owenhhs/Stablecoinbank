package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelPayInfoCfgEntity;


@Data
public class PaymentChannelPayInfoCfgVO extends PaymentChannelPayInfoCfgEntity {

    private Long payBusinessId;

    private String merName;

}