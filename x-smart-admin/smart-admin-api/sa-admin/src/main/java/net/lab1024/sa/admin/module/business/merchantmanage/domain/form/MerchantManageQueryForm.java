package net.lab1024.sa.admin.module.business.merchantmanage.domain.form;

import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * 商户表 分页查询表单
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Data
public class MerchantManageQueryForm extends PageParam{

    private Long departmentId;


    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;
}