package net.lab1024.sa.admin.module.business.payment.channel.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentUserQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentUserVO;
import net.lab1024.sa.admin.module.business.payment.channel.service.PaymentUserService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author 孙宇
 * @date 2025/04/14 21:32
 */
@Slf4j
@RestController
@Tag(name = AdminSwaggerTagConst.Business.MERCHANT_MANAGE_USER)
public class PaymentUserController {

    @Resource
    private PaymentUserService paymentUserService;

    @Operation(summary = "分页查询 @author sunyu")
    @PostMapping("/payment-user/queryPage")
    public ResponseDTO<PageResult<PaymentUserVO>> queryPage(@RequestBody @Valid PaymentUserQueryForm queryForm) {
        return ResponseDTO.ok(paymentUserService.queryPage(queryForm));
    }

    @Operation(summary = "更新用户黑名单")
    @SaCheckPermission("payment:user:black")
    @PostMapping("/payment-user/black/update")
    public ResponseDTO<String> updateUserBlack(@RequestParam("userId") Long userId, @RequestParam("black") Integer black) {
        paymentUserService.updateUserBlack(userId, black);
        return ResponseDTO.ok();
    }

}
