package net.lab1024.sa.admin.module.system.login.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class LoginOtpVerifyForm {

    @Schema(description = "临时凭证")
    @NotEmpty(message = "临时凭证不能为空")
    private String scrip;

    @Schema(description = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String code;

}
