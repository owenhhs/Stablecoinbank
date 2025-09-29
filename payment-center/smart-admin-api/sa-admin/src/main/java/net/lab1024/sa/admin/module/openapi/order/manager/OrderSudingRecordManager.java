package net.lab1024.sa.admin.module.openapi.order.manager;

import com.alibaba.fastjson.JSONObject;
import net.lab1024.sa.admin.emumeration.NoahPaymentFlagEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.dao.OrderSudingRecordDao;
import net.lab1024.sa.admin.module.openapi.order.domain.entity.OrderSudingRecordEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 速鼎订单流水  Manager
 *
 * @Author Sunny
 * @Date 2024-09-04 15:18:34
 * @Copyright Sunny
 */
@Service
public class OrderSudingRecordManager extends ServiceImpl<OrderSudingRecordDao, OrderSudingRecordEntity> {

    public OrderSudingRecordEntity save(PaymentChannelInfoVO paymentChannelInfoVO, Map<String, Object> params, JSONObject resultObj) {
        OrderSudingRecordEntity orderSudingRecordEntity = new OrderSudingRecordEntity();
        orderSudingRecordEntity.setChannelId(paymentChannelInfoVO.getId());
        orderSudingRecordEntity.setOrderNo((String) params.get("cus_order_sn"));
        orderSudingRecordEntity.setTradeType(PaymentTypeEnum.payment.getValue());
        orderSudingRecordEntity.setPaymentFlag((String) params.get("payment_flag"));
        orderSudingRecordEntity.setStatus(resultObj.getInteger("status"));
        orderSudingRecordEntity.setResult(resultObj.getString("result"));

        if ("success".equals(orderSudingRecordEntity.getResult())) {
            orderSudingRecordEntity.setMessage(resultObj.getString("message"));
            orderSudingRecordEntity.setOrderSn(resultObj.getString("order_sn"));
            orderSudingRecordEntity.setCurrencyType(resultObj.getString("currency_type"));
            orderSudingRecordEntity.setOrderAmount(resultObj.getBigDecimal("order_amount"));
            orderSudingRecordEntity.setOriginalAmount(resultObj.getBigDecimal("original_amount"));
            orderSudingRecordEntity.setExchangeAmount(resultObj.getBigDecimal("exchange_amount"));
            orderSudingRecordEntity.setPaymentUri(resultObj.getString("payment_uri"));
            orderSudingRecordEntity.setPaymentImg(resultObj.getString("payment_img"));

            JSONObject extraData = resultObj.getJSONObject("extra_data");
            if (extraData != null) {
                JSONObject cardInfo = extraData.getJSONObject("card_info");
                if (cardInfo != null) {
                    if (NoahPaymentFlagEnum.bank.getPaymentFlag().equals(orderSudingRecordEntity.getPaymentFlag())) {
                        orderSudingRecordEntity.setBankName(cardInfo.getString("bank_name"));
                        orderSudingRecordEntity.setBankBranch(cardInfo.getString("bank_branch"));
                        orderSudingRecordEntity.setBankAccount(cardInfo.getString("bank_account"));
                        orderSudingRecordEntity.setBankAccountName(cardInfo.getString("bank_account_name"));
                    } else {
                        orderSudingRecordEntity.setOwnerName(cardInfo.getString("owner_name"));
                        orderSudingRecordEntity.setOwnerAccount(cardInfo.getString("owner_account"));
                        orderSudingRecordEntity.setOwnerNickName(cardInfo.getString("owner_nickname"));
                        orderSudingRecordEntity.setOwnerPhoneNumber(cardInfo.getString("owner_phone_number"));
                        orderSudingRecordEntity.setOwnerUrlLink(cardInfo.getString("owner_url_link"));
                        orderSudingRecordEntity.setOwnerQrcode(cardInfo.getString("owner_qrcode"));
                        orderSudingRecordEntity.setOwnerQrcodeUrl(cardInfo.getString("owner_qrcode_url"));
                    }
                }
            }
        } else {
            orderSudingRecordEntity.setRequestData(resultObj.getString("request_data"));
        }
        orderSudingRecordEntity.setCreateTime(LocalDateTime.now());
        orderSudingRecordEntity.setUpdateTime(LocalDateTime.now());
        this.save(orderSudingRecordEntity);
        return orderSudingRecordEntity;
    }

}
