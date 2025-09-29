package net.lab1024.sa.admin.module.openapi.order.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderFailRecordManager;
import net.lab1024.sa.admin.module.openapi.order.domain.form.*;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawDetailsVO;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import net.lab1024.sa.base.common.annoation.ApiAuth;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.domain.RequestMerchant;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiDecrypt;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiEncrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author 孙宇
 * @date 2024/07/25 22:42
 */
@Slf4j
@RestController
@Tag(name = "")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private PaymentOrderFailRecordManager paymentOrderFailRecordManager;

    /*####################################*
     * 入金相关接口
     *####################################*/

    @ApiAuth
    @Operation(summary = "存款申请 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/deposit")
    public ResponseDTO<OrderDepositVO> orderDeposit(@RequestBody @Valid OrderDepositForm depositForm) throws Exception {
        log.info("存款申请入参, orderNo:{}, params:{}", depositForm.getOrderNo(), depositForm);
        depositForm.checkParams();
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        ResponseDTO<OrderDepositVO> responseDTO = orderService.orderDeposit(requestMerchant, depositForm);
        log.info("存款申请出参, orderNo:{}, response:{}", depositForm.getOrderNo(), responseDTO);
        if (!responseDTO.getOk()) {
            paymentOrderFailRecordManager.savePaymentOrderFailRecord(depositForm.getOrderNo(), JSONObject.toJSONString(depositForm), JSONObject.toJSONString(responseDTO));
            orderService.orderFailSendSms();
        }
        return responseDTO;
    }

    @ApiAuth
    @Operation(summary = "存款订单查询 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/depositDetails")
    public ResponseDTO<OrderDepositDetailsVO> orderDepositDetails(@RequestBody @Valid OrderDepositDetailsForm depositForm) {
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderDepositDetail(requestMerchant, depositForm);
    }


    // 确认付款-上传付款凭证
    @ApiAuth
    @Operation(summary = "确认付款-上传付款凭证 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/payment/confirm")
    public ResponseDTO<String> confirmPaymentOrderLyV1(@RequestParam String orderNo, @RequestParam(required = false) MultipartFile file) {
        return orderService.paymentConfirm(orderNo, file, null);
    }

    // 取消付款
    @ApiAuth
    @Operation(summary = "取消付款 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/payment/cancel")
    public ResponseDTO<String> cancelPaymentOrderLyV1(@RequestBody OrderDepositDetailsForm form) {
        return orderService.paymentCancel(form.getOrderNo(), null);
    }


    @ApiAuth
    @ApiEncrypt
    @ApiDecrypt
    @Operation(summary = "存款申请 加密版 @author sunyu")
    @PostMapping("/openapi/order/v2/ly/commgr/deposit")
    public ResponseDTO<OrderDepositVO> orderDepositV2(@RequestBody @Valid OrderDepositForm depositForm) throws Exception {
        log.info("存款申请入参 v2, orderNo:{}, params:{}", depositForm.getOrderNo(), depositForm);
        depositForm.checkParams();
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        ResponseDTO<OrderDepositVO> responseDTO = orderService.orderDeposit(requestMerchant, depositForm);
        log.info("存款申请出参 v2, orderNo:{}, response:{}", depositForm.getOrderNo(), responseDTO);
        if (!responseDTO.getOk()) {
            paymentOrderFailRecordManager.savePaymentOrderFailRecord(depositForm.getOrderNo(), JSONObject.toJSONString(depositForm), JSONObject.toJSONString(responseDTO));
            orderService.orderFailSendSms();
        }
        return responseDTO;
    }

    @ApiAuth
    @ApiEncrypt
    @ApiDecrypt
    @Operation(summary = "存款订单查询 @author sunyu")
    @PostMapping("/openapi/order/v2/ly/commgr/depositDetails")
    public ResponseDTO<OrderDepositDetailsVO> orderDepositDetailsV2(@RequestBody @Valid OrderDepositDetailsForm depositForm) {
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderDepositDetail(requestMerchant, depositForm);
    }


    // 确认付款-上传付款凭证
    @ApiAuth
    @ApiEncrypt
    @ApiDecrypt
    @Operation(summary = "确认付款-上传付款凭证 @author sunyu")
    @PostMapping("/openapi/order/v2/ly/payment/confirm")
    public ResponseDTO<String> confirmPaymentOrderLyV2(@RequestParam String orderNo, @RequestParam(required = false) MultipartFile file) {
        return orderService.paymentConfirm(orderNo, file, null);
    }

    // 取消付款
    @ApiAuth
    @ApiEncrypt
    @ApiDecrypt
    @Operation(summary = "取消付款 @author sunyu")
    @PostMapping("/openapi/order/v2/ly/payment/cancel")
    public ResponseDTO<String> cancelPaymentOrderLyV2(@RequestBody OrderDepositDetailsForm form) {
        return orderService.paymentCancel(form.getOrderNo(), null);
    }


    @Operation(summary = "获取订单信息 @author sunyu")
    @GetMapping("/openapi/order/payment/cashier/info")
    public ResponseDTO<PaymentOrderInfoVO> getPaymentOrderInfo(@RequestParam String token) throws Exception {
        return orderService.getPaymentOrderInfo(token);
    }

    // 确认付款-上传付款凭证
    @Operation(summary = "确认付款-上传付款凭证 @author sunyu")
    @PostMapping("/openapi/order/payment/cashier/confirm")
    public ResponseDTO<String> confirmPaymentOrder(@RequestParam String token, @RequestParam(required = false) MultipartFile file) {
        return orderService.confirmPaymentOrder(token, file);
    }

    // 取消付款
    @Operation(summary = "取消付款 @author sunyu")
    @PostMapping("/openapi/order/payment/cashier/cancel")
    public ResponseDTO<String> cancelPaymentOrder(@RequestParam String token) {
        return orderService.cancelPaymentOrder(token);
    }

    @Operation(summary = "取消支付 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/payment/cancel")
    public ResponseDTO<String> paymentCancel(@RequestBody @Valid OrderDepositPayStatusForm payStatusForm) {
        return orderService.payStatus(payStatusForm);
    }

    @Operation(summary = "支付确认 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/payment/affirm")
    public ResponseDTO<String> paymentAffirm(@RequestParam String token, @RequestParam MultipartFile file) {
        return orderService.paymentAffirm(token, file);
    }

    @Operation(summary = "切换二维码 @author sunyu")
    @GetMapping("/openapi/order/v1/ly/commgr/payment/switch")
    public ResponseDTO<String> paymentSwitch(@RequestParam String token) {
        return orderService.paymentSwitch(token);
    }

    @Operation(summary = "支付方式选择 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/payment/choose")
    public ResponseDTO<OrderDepositVO> paymentChoose(@RequestParam String token, @RequestParam String paymentChannel) throws Exception {
        return orderService.paymentChoosed(token, paymentChannel);
    }

    @Operation(summary = "测试专用回调接口 @author sunyu")
    @PostMapping("/test-callback/order/notity/test")
    public ResponseDTO<String> notity(@RequestBody String body) {
        log.info("测试专用回调接口, body:{}", body);
        return ResponseDTO.ok();
    }

    /*####################################*
     * 出金相关接口
     *####################################*/
    @ApiAuth
    @Operation(summary = "出金申请")
    @PostMapping("/openapi/order/v2/ly/commgr/withdraw")
    public ResponseDTO<String> orderWithdraw(@RequestBody @Valid OrderWithdrawForm form) throws Exception {
        form.checkParams();
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderWithdraw(requestMerchant, form);
    }

    @ApiAuth
    @Operation(summary = "出金订单查询")
    @PostMapping("/openapi/order/v2/ly/commgr/withdrawDetails")
    public ResponseDTO<OrderWithdrawDetailsVO> orderWithdrawDetails(@RequestBody @Valid OrderWithdrawDetailsForm form) {
        RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderWithdrawDetail(requestMerchant, form);
    }

    @NoNeedLogin
    @Operation(summary = "数据清洗接口接口 @author sunyu")
    @PostMapping("/test-data/encrypt/run")
    public ResponseDTO<String> encrypt() {
        log.info("数据清洗接口接口");
        orderService.dataEncrypt();
        return ResponseDTO.ok();
    }
}
