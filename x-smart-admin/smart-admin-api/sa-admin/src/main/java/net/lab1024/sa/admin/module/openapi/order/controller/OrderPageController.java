package net.lab1024.sa.admin.module.openapi.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 孙宇
 * @date 2024/07/25 22:42
 */
@Controller
@Tag(name = "")
public class OrderPageController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "订单支付页 @author sunyu")
    @GetMapping("/zfx/order/payment/{token}")
    public String paymentPage(Model model, @PathVariable String token, @RequestHeader("user-agent") String userAgent) {
        return orderService.paymentPage(token, model, userAgent);
    }

}
