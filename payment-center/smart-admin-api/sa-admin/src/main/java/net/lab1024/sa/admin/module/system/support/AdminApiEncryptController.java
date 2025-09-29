package net.lab1024.sa.admin.module.system.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.base.common.annoation.ApiAuth;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.controller.SupportBaseController;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.constant.SwaggerTagConst;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiDecrypt;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiEncrypt;
import net.lab1024.sa.base.module.support.sensitive.annoation.Sensitive;
import net.lab1024.sa.base.module.support.sensitive.enumeration.SensitiveType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * api 加密
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 09:21:20
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
@Slf4j
@RestController
@Tag(name = SwaggerTagConst.Support.PROTECT)
public class AdminApiEncryptController extends SupportBaseController {

    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private PaymentOrderInfoDao paymentOrderInfoDao;


    @ApiDecrypt
    @PostMapping("/apiEncrypt/testRequestEncrypt")
    @Operation(summary = "测试 请求加密")
    @NoNeedLogin
    public ResponseDTO<JweForm> testRequestEncrypt(@RequestBody @Valid JweForm form) {
        return ResponseDTO.ok(form);
    }

    @NoNeedLogin
    @ApiAuth
    @ApiEncrypt
    @ApiDecrypt
    @PostMapping("/apiEncrypt/testResponseEncrypt")
    @Operation(summary = "测试 返回加密")
    public ResponseDTO<JweForm> testResponseEncrypt(@RequestBody @Valid JweForm form) {
        log.info("testResponseEncrypt. form={}", form);
        log.info("testResponseEncrypt. form={}", JSONObject.toJSONString(form));
        log.info("testResponseEncrypt. form={}", JSON.toJSONString(form));
        log.info("\"mobile\":13810293746, \"address\":啊是哒好滴啦几十块打了打, \"name\":阿斯达");
        log.info("mobile:13810293746; address:啊是哒好滴啦几十块打了打; name:阿斯达");


        PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.getById(176);
        log.info("orderInfoEntity={}", JSON.toJSONString(orderInfoEntity));

        PaymentOrderInfoEntity paymentOrderInfoEntity = paymentOrderInfoDao.selectById(176);
        log.info("paymentOrderInfoEntity={}", JSON.toJSONString(paymentOrderInfoEntity));

        return ResponseDTO.ok(form);
    }

    @NoNeedLogin
    @ApiDecrypt
    @ApiEncrypt
    @PostMapping("/apiEncrypt/testDecryptAndEncrypt")
    @Operation(summary = "测试 请求参数加密和解密、返回数据加密和解密")
    public ResponseDTO<JweForm> testDecryptAndEncrypt(@RequestBody @Valid JweForm form) {
        return ResponseDTO.ok(form);
    }

    @NoNeedLogin
    @ApiDecrypt
    @ApiEncrypt
    @PostMapping("/apiEncrypt/testArray")
    @Operation(summary = "测试 数组加密和解密")
    public ResponseDTO<List<JweForm>> testArray(@RequestBody @Valid ValidateList<JweForm> list) {
        return ResponseDTO.ok(list);
    }


    @Data
    public static class JweForm {

        @Sensitive(type = SensitiveType.CUSTOM, suffix = 2, prefix = 1, replaceChar = "#")
        @NotBlank(message = "姓名 不能为空")
        String name;

        @Sensitive(type = SensitiveType.PHONE)
        String mobile;

        @NotNull
        @Min(value = 1)
        Integer age;

    }

}
