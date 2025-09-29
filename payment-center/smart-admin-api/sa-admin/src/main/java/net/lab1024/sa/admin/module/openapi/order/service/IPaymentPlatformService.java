package net.lab1024.sa.admin.module.openapi.order.service;

import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.base.common.util.SpringBootBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/09/04 21:28
 */
public interface IPaymentPlatformService {

    PaymentApplyResultVO apply(PaymentChannelInfoVO paymentChannelInfoVO, OrderDepositForm depositForm);

    ReceiverInfoVO getReceiverInfo(String orderNo);

    boolean signVerify(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params);

    OrderDetailVO updateOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params);

    OrderDetailVO getPaymentOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo);

    PaymentApplyResultVO paymentSwitch(PaymentChannelInfoEntity channelInfoVO, String orderNo);

    /**
     * 收银台获取订单信息
     * @param channelInfoVO
     * @param orderNo
     * @return
     */
    PaymentOrderInfoVO getPaymentOrderInfo(PaymentChannelInfoEntity channelInfoVO, String orderNo);

    /**
     * 收银台检查订单状态
     * @param channelInfoVO
     * @param orderNo
     */
//    void checkPaymentOrderStatus(PaymentChannelInfoEntity channelInfoVO, String orderNo);

    /**
     * 收银台确认支付并上传凭证
     * @param channelInfoVO
     * @param orderNo
     */
    void paymentReceipt(PaymentChannelInfoEntity channelInfoVO, String orderNo, MultipartFile file);

    /**
     * 收银台取消支付
     * @param channelInfoVO
     * @param orderNo
     */
    void paymentCancel(PaymentChannelInfoEntity channelInfoVO, String orderNo);


    /**
     * 【接口】出金订单申请
     * @param paymentChannelInfoVO
     * @param form
     * @param subOrderNo
     * @return
     */
    PaymentApplyResultVO applyForWithdraw(PaymentChannelInfoVO paymentChannelInfoVO, OrderWithdrawForm form, String subOrderNo);

    /**
     * 【接口】出金订单查询
     * @param channelInfoVO
     * @param orderNo
     * @return
     */
    OrderWithdrawDetailVO getWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, String orderNo);

    /**
     * 出金订单（更新数据表）
     * @param channelInfoVO
     * @param params
     * @return
     */
    OrderWithdrawDetailVO updateWithdrawOrderDetail(PaymentChannelInfoEntity channelInfoVO, Map<String, Object> params);

    static IPaymentPlatformService get(String implCode) {
        PaymentPlatformEnum platformEnum = PaymentPlatformEnum.getByValue(implCode);
        return SpringBootBeanUtil.getBean(platformEnum.getPaymentPlatformServiceClass());
    }

    static Map<String, Object> filterParams(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof String && StringUtils.isEmpty((String) value)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
}
