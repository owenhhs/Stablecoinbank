package net.lab1024.sa.admin.module.business.payment.channel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 订单信息表 新建表单
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Data
public class QueryOptionsForm {

    @Schema(description = "部门id(商户id)")
    private List<Long> departmentIds;


    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;


}