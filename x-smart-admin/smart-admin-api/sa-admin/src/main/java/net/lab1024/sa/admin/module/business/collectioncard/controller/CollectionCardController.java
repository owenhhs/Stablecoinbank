package net.lab1024.sa.admin.module.business.collectioncard.controller;

import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardAddForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardQueryForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.form.CollectionCardUpdateForm;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardOptionsVO;
import net.lab1024.sa.admin.module.business.collectioncard.domain.vo.CollectionCardVO;
import net.lab1024.sa.admin.module.business.collectioncard.service.CollectionCardService;
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
import java.util.List;

/**
 * 收款银行卡表 Controller
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:43
 * @Copyright sunyu
 */

@OperateLog
@RestController
@Tag(name = "")
public class CollectionCardController {

    @Resource
    private CollectionCardService collectionCardService;

    @Operation(summary = "分页查询 @author sunyu")
    @PostMapping("/collectionCard/queryPage")
    public ResponseDTO<PageResult<CollectionCardVO>> queryPage(@RequestBody @Valid CollectionCardQueryForm queryForm) {
        return ResponseDTO.ok(collectionCardService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author sunyu")
    @PostMapping("/collectionCard/add")
    public ResponseDTO<String> add(@RequestBody @Valid CollectionCardAddForm addForm) {
        return collectionCardService.add(addForm);
    }

    @Operation(summary = "更新 @author sunyu")
    @PostMapping("/collectionCard/update")
    public ResponseDTO<String> update(@RequestBody @Valid CollectionCardUpdateForm updateForm) {
        return collectionCardService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author sunyu")
    @PostMapping("/collectionCard/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return collectionCardService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author sunyu")
    @GetMapping("/collectionCard/delete/{id}")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return collectionCardService.delete(id);
    }

    @Operation(summary = "选项列表 @author sunyu")
    @PostMapping("/collectionCard/options")
    public ResponseDTO<List<CollectionCardOptionsVO>> options() {
        return ResponseDTO.ok(collectionCardService.options());
    }
}
