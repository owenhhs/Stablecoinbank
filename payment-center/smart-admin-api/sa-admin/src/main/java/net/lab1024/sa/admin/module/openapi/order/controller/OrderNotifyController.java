package net.lab1024.sa.admin.module.openapi.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import net.lab1024.sa.base.common.annoation.IpWhitelist;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/09/05 17:02
 */
@Slf4j
@RestController
public class OrderNotifyController {

    @Resource
    private OrderService orderService;


    @Operation(summary = "noah支付订单回调接口 @author sunyu")
    @PostMapping("/notify/noah/payment/order/result")
    public Map<String, String> notifyNoahPaymentOrder(@RequestBody Map<String, Object> params) {

        return orderService.notifyNoahPaymentOrder(params);
    }


    @Operation(summary = "Letonggou支付订单回调接口 @author sunyu")
    @GetMapping("/notify/payment/order/result")
    public String notifyLetonggouPaymentOrder(@RequestParam("pid") Integer pid, @RequestParam("trade_no") String tradeNo,
                                              @RequestParam("out_trade_no") String outTradeNo, @RequestParam("type") String type,
                                              @RequestParam("name") String name, @RequestParam("money") String money,
                                              @RequestParam("trade_status") String tradeStatus, @RequestParam(value = "param", required = false) String param,
                                              @RequestParam("sign") String sign, @RequestParam("sign_type") String signType) {
        return orderService.notifyLetonggouPaymentOrder(pid, tradeNo, outTradeNo, type, name, money, tradeStatus, param, sign, signType);
    }


    @Operation(summary = "fxmch渠道管理平台回调接口 @author sunyu")
    @GetMapping("/notify/fxmch/payment/order/result")
    public ResponseDTO notifyFxmchPaymentOrder(@RequestParam("orderNo") String orderNo, @RequestParam("status") Integer status, @RequestHeader("Authorization") String authorization,
                                               @RequestHeader("Date") String date) {
        return orderService.notifyFxmchPaymentOrder(orderNo, status, authorization, date);
    }

    @Operation(summary = "payrock回调接口 @author sunyu")
    @PostMapping("/notify/payrock/payment/order/result")
    public ResponseDTO notifyPayrockPaymentOrder(@RequestParam("cartId") String cartId,
                                                 @RequestParam("orderAmount") String orderAmount,
                                                 @RequestParam("originalAmount") String originalAmount,
                                                 @RequestParam("signature") String signature,
                                                 @RequestParam("orderTime") String orderTime,
                                                 @RequestParam("completedTime") String completedTime,
                                                 @RequestParam("paymentReference") String paymentReference,
                                                 @RequestParam("currency") String currency,
                                                 @RequestParam("version") String version,
                                                 @RequestParam("statusMessage") String statusMessage,
                                                 @RequestParam("status") String status) {
        log.info("notifyPayrockPaymentOrder, cartId:{}, orderAmount:{}, originalAmount:{}, orderTime:{}, completedTime:{}, " +
                "paymentReference:{}, currency:{}, statusMessage:{}, status:{}", cartId, orderAmount, originalAmount,
                orderTime, completedTime, paymentReference, currency, statusMessage, status);
        return orderService.notifyPayrockPaymentOrder(cartId, orderAmount, originalAmount,
                signature, orderTime, completedTime, paymentReference, currency, version, statusMessage, status);
    }


    @IpWhitelist
    @Operation(summary = "ltg回调接口 @author sunyu")
    @PostMapping("/notify/ltg/payment/order/result")
    public String notifyLtgPaymentOrder(@RequestParam Map<String, Object> params) {
        log.info("notifyLtgPaymentOrder, params:{}", params);
        return orderService.notifyLtgPaymentOrder(params);
    }

    @Operation(summary = "eyp回调接口 @author sunyu")
    @PostMapping("/notify/eyp/payment/order/result")
    public String notifyEypPaymentOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyEypPaymentOrder, body:{}", body);
        return orderService.notifyEypPaymentOrder(body);
    }


    @Operation(summary = "eyp出金回调接口 @author sunyu")
    @PostMapping("/notify/eyp/cash/order/result")
    public String notifyEypCashOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyEypCashOrder, body:{}", body);
        return orderService.notifyEypCashOrder(body);
    }


    @Operation(summary = "lyp回调接口 @author sunyu")
    @PostMapping("/notify/lyp/payment/order/result")
    public String notifyLypPaymentOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyLypPaymentOrder, body:{}", body);
        return orderService.notifyLypPaymentOrder(body);
    }

    @Operation(summary = "lyp出金回调接口 @author sunyu")
    @PostMapping("/notify/lyp/cash/order/result")
    public String notifyLypCashOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyLypCashOrder, body:{}", body);
        return orderService.notifyLypCashOrder(body);
    }


    @Operation(summary = "np回调接口 @author sunyu")
    @PostMapping("/notify/np/payment/order/result")
    public String notifyNpPaymentOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyNpPaymentOrder, body:{}", body);
        String result = orderService.notifyNpPaymentOrder(body);
        log.info("notifyNpPaymentOrder, orderNo:{}, result:{}", body.get("bill_no"), result);
        return result;
    }

    @Operation(summary = "np出金回调接口 @author sunyu")
    @PostMapping("/notify/np/cash/order/result")
    public String notifyNpCashOrder(@RequestBody Map<String, Object> body) {
        log.info("notifyNpCashOrder, body:{}", body);
        String result = orderService.notifyNpCashOrder(body);
        log.info("notifyNpCashOrder,  orderNo:{}, result:{}", body.get("bill_no"), result);
        return result;
    }


    //    测试接口
//    @Operation(summary = "Letonggou支付订单回调接口 @author sunyu")
//    @GetMapping("/notify/payment/order/xxxxxxxx")
//    public ResponseDTO notifyLetonggouPaymentOrder(@RequestParam("out_trade_no") String outTradeNo, @RequestParam("status") String status) {
//        return orderService.notifyXxxxxxxxPaymentOrder(outTradeNo, status);
//    }

    /*####################################*
     * 出金相关回调接口
     *####################################*/
//    @Operation(summary = "出金订单回调接口示例")
//    @PostMapping("/notify/xxx/withdraw/order/result")
    public ResponseDTO notifyXXXWithdrawOrder(@RequestParam("cartId") String cartId,
                                                 @RequestParam("orderAmount") String orderAmount,
                                                 @RequestParam("originalAmount") String originalAmount,
                                                 @RequestParam("signature") String signature,
                                                 @RequestParam("orderTime") String orderTime,
                                                 @RequestParam("completedTime") String completedTime,
                                                 @RequestParam("paymentReference") String paymentReference,
                                                 @RequestParam("currency") String currency,
                                                 @RequestParam("version") String version,
                                                 @RequestParam("statusMessage") String statusMessage,
                                                 @RequestParam("status") String status) {
        log.info("notifyXXXWithdrawOrder, cartId:{}, orderAmount:{}, originalAmount:{}, orderTime:{}, completedTime:{}, " +
                        "paymentReference:{}, currency:{}, statusMessage:{}, status:{}", cartId, orderAmount, originalAmount,
                orderTime, completedTime, paymentReference, currency, statusMessage, status);
        return orderService.notifyXXXWithdrawOrder(cartId, orderAmount, originalAmount,
                signature, orderTime, completedTime, paymentReference, currency, version, statusMessage, status);
    }
}
