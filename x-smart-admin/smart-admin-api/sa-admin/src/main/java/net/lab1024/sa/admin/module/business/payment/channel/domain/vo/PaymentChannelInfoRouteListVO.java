package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;


@Data
public class PaymentChannelInfoRouteListVO extends PaymentChannelInfoEntity {

   private Long paymentChannelBusinessId;

}