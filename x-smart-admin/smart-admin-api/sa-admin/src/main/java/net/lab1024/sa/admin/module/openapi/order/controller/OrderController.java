package net.lab1024.sa.admin.module.openapi.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleVO;
import net.lab1024.sa.admin.module.business.settle.service.SettleService;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositDetailsForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderDepositDetailsVO;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderDepositVO;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.annoation.ApiAuth;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.sms.SmsService;
import net.lab1024.sa.base.module.support.sms.domain.SendSmsRequestVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author 孙宇
 * @date 2024/07/25 22:42
 */
@Slf4j
//@ApiAuth
@RestController
@Tag(name = "")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private SmsService smsService;

    @Operation(summary = "存款申请 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/deposit")
    public ResponseDTO<OrderDepositVO> orderDeposit(@RequestBody @Valid OrderDepositForm depositForm) throws Exception {
        //depositForm.checkParams();
        //RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderDeposit(depositForm);
    }

    @Operation(summary = "存款订单查询 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/depositDetails")
    public ResponseDTO<OrderDepositDetailsVO> orderDepositDetails(@RequestBody @Valid OrderDepositDetailsForm depositForm) {
        //RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return orderService.orderDepositDetail(depositForm);
    }


    @Operation(summary = "存款确认回单 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/deposit/receipt")
    public ResponseDTO<String> orderDepositReceipt(@RequestParam MultipartFile file, @RequestParam String orderNo){
        //RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        orderService.orderDepositReceipt(file, orderNo);
        return ResponseDTO.ok();
    }

    @Operation(summary = "订单取消 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/deposit/cancel")
    public ResponseDTO<String> orderDepositReceipt(@RequestBody @Valid OrderDepositDetailsForm depositForm){
        //RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        orderService.orderDepositCancel(depositForm.getOrderNo());
        return ResponseDTO.ok();
    }

    @Operation(summary = "切换付款码 @author sunyu")
    @PostMapping("/openapi/order/v1/ly/commgr/deposit/switch")
    public ResponseDTO<OrderDepositVO> orderDepositSwitch(@RequestBody @Valid OrderDepositDetailsForm depositForm){
        //RequestMerchant requestMerchant = SmartRequestUtil.getRequestMerchant();
        return ResponseDTO.ok(orderService.orderDepositSwitch(depositForm.getOrderNo()));
    }


//    @Operation(summary = "取消支付 @author sunyu")
//    @PostMapping("/openapi/order/v1/ly/commgr/payment/cancel")
//    public ResponseDTO<String> paymentCancel(@RequestBody @Valid OrderDepositPayStatusForm payStatusForm) {
//        return orderService.payStatus(payStatusForm);
//    }
//
//    @Operation(summary = "支付确认 @author sunyu")
//    @PostMapping("/openapi/order/v1/ly/commgr/payment/affirm")
//    public ResponseDTO<String> paymentAffirm(@RequestParam MultipartFile file, @RequestParam String token) {
//        return orderService.paymentAffirm(file, token);
//    }

    @NoNeedLogin
    @Operation(summary = "数据清洗接口接口 @author sunyu")
    @PostMapping("/test-data/encrypt/run")
    public ResponseDTO<String> encrypt() {
        log.info("数据清洗接口接口");
        orderService.dataEncrypt();
        return ResponseDTO.ok();
    }


    @Resource
    private SettleService settleService;

    /**
     * 结算列表，带分页
     */
    @Operation(summary = "结算列表")
    @PostMapping("/openapi/settle/queryPageBySettle")
    public ResponseDTO<PageResult<SettleVO>> queryPageBySettle(@RequestBody @Valid SettleQueryForm queryForm) {

        RequestEmployee requestEmployee = new RequestEmployee();
        requestEmployee.setAdministratorFlag(true);
        requestEmployee.setEmployeeId(1L);
        requestEmployee.setDepartmentId(1L);
        SmartRequestUtil.setRequestUser(requestEmployee);
        queryForm.setDepartmentId(1L);
        return ResponseDTO.ok(settleService.queryPageBySettle(queryForm));
    }


    @Operation(summary = "发送短信")
    @GetMapping("/openapi/sms/send")
    public ResponseDTO<String> sendSms(@RequestParam("phone") String phone, @RequestParam("smsType") Integer smsType, @RequestParam("businessType") String businessType) {
        SendSmsRequestVO sendSmsRequestVO = new SendSmsRequestVO();
        sendSmsRequestVO.setPhoneNumber(phone);
        boolean sendSuccess = smsService.sendSms(sendSmsRequestVO, smsType, businessType);
        if (!sendSuccess){
            log.error("sms send fail phone:{}, smsType:{}, businessType:{}", phone, smsType, businessType);
            return ResponseDTO.error(SystemErrorCode.SMS_SEND_ERROR);
        }
        return ResponseDTO.ok();
    }
}
