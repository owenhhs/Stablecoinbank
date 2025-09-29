package net.lab1024.sa.admin.module.system.employee.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 员工多因子信息 实体表
 */
@Data
@TableName("t_employee_otp")
public class EmployeeOtpEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    /**
     * 秘钥
     */
    private String secretKey;

    /**
     * 是否绑定 0否 1是
     */
    private Boolean boundFlag;

    /**
     * 是否启用 0否1是
     */
    private Boolean enabledFlag;

    /**
     * 是否删除 0否 1是
     */
    private Boolean deletedFlag;


    private LocalDateTime updateTime;

    private LocalDateTime createTime;


}
