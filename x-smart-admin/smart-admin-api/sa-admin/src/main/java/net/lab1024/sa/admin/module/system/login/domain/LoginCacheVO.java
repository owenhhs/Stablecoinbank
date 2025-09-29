package net.lab1024.sa.admin.module.system.login.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.constant.LoginDeviceEnum;


@Data
public class LoginCacheVO {

    @Schema(description = "employeeId")
    private Long employeeId;

    @SchemaEnum(desc = "登录终端", value = LoginDeviceEnum.class)
    private Integer loginDevice;
}
