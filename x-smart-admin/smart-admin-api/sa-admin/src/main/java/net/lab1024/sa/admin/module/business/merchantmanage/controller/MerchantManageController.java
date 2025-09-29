package net.lab1024.sa.admin.module.business.merchantmanage.controller;

import net.lab1024.sa.admin.module.business.merchantmanage.domain.form.MerchantManageAddForm;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.form.MerchantManageQueryForm;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.form.MerchantManageUpdateForm;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.vo.MerchantManageVO;
import net.lab1024.sa.admin.module.business.merchantmanage.service.MerchantManageService;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeDao;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 商户表 Controller
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@OperateLog
@RestController
@Tag(name = "")
public class MerchantManageController {

    @Resource
    private MerchantManageService merchantManageService;
    @Resource
    private EmployeeDao employeeDao;

    @Operation(summary = "分页查询 @author sunyu")
    @PostMapping("/merchantManage/queryPage")
    public ResponseDTO<PageResult<MerchantManageVO>> queryPage(@RequestBody @Valid MerchantManageQueryForm queryForm) {
        //如果有商户id，那么就使用商户id，如果没有就取当前用户关联的商户id
        if(StringUtils.isEmpty(queryForm.getDepartmentId())){
            RequestUser requestUser = SmartRequestUtil.getRequestUser();
            //Long userId = 1l;//requestUser.getUserId();
            EmployeeEntity employeeEntity = employeeDao.selectById(requestUser.getUserId());
            queryForm.setDepartmentId(employeeEntity.getDepartmentId());
            queryForm.setAdministratorFlag(employeeEntity.getAdministratorFlag());
        }
        return ResponseDTO.ok(merchantManageService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author sunyu")
    @PostMapping("/merchantManage/add")
    public ResponseDTO<String> add(@RequestBody @Valid MerchantManageAddForm addForm) {
        return merchantManageService.add(addForm);
    }

    @Operation(summary = "更新 @author sunyu")
    @PostMapping("/merchantManage/update")
    public ResponseDTO<String> update(@RequestBody @Valid MerchantManageUpdateForm updateForm) {
        return merchantManageService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author sunyu")
    @PostMapping("/merchantManage/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return merchantManageService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author sunyu")
    @GetMapping("/merchantManage/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return merchantManageService.delete(id);
    }
}
