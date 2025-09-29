package net.lab1024.sa.admin.module.business.skucfg.controller;

import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgAddForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgQueryForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.form.SkuCfgUpdateForm;
import net.lab1024.sa.admin.module.business.skucfg.domain.vo.SkuCfgVO;
import net.lab1024.sa.admin.module.business.skucfg.service.SkuCfgService;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
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
 * 商品配置 Controller
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */
@OperateLog
@RestController
@Tag(name = "")
public class SkuCfgController {

    @Resource
    private SkuCfgService skuCfgService;

    @Operation(summary = "分页查询 @author sunyu")
    @PostMapping("/skuCfg/queryPage")
    public ResponseDTO<PageResult<SkuCfgVO>> queryPage(@RequestBody @Valid SkuCfgQueryForm queryForm) {
        return ResponseDTO.ok(skuCfgService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author sunyu")
    @PostMapping("/skuCfg/add")
    public ResponseDTO<String> add(@RequestBody @Valid SkuCfgAddForm addForm) {
        return skuCfgService.add(addForm);
    }

    @Operation(summary = "更新 @author sunyu")
    @PostMapping("/skuCfg/update")
    public ResponseDTO<String> update(@RequestBody @Valid SkuCfgUpdateForm updateForm) {
        return skuCfgService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author sunyu")
    @PostMapping("/skuCfg/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return skuCfgService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author sunyu")
    @GetMapping("/skuCfg/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return skuCfgService.delete(id);
    }
}
