package net.lab1024.sa.base.module.support.searchlog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.controller.SupportBaseController;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.constant.SwaggerTagConst;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.searchlog.domain.SearchLogRes;
import net.lab1024.sa.base.module.support.searchlog.service.SearchLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 孙宇
 * @date 2025/03/17 12:00
 */
@Slf4j
@Tag(name = SwaggerTagConst.Support.SEARCH_LOG)
@RestController
public class SearchLogController extends SupportBaseController {

    @Resource
    private SearchLogService searchLogService;

    @OperateLog
    @Operation(summary = "日志查询-搜索 @author Sunny")
    @GetMapping("/log/search")
    @SaCheckPermission("support:searchLog:query")
    public ResponseDTO<List<SearchLogRes>> search(@RequestParam String keyword,
                                                  @RequestParam(required = false) String date,
                                                  @RequestParam(required = false, defaultValue = "100") Integer count,
                                                  @RequestParam(required = false, defaultValue = "all") String type) {
        return searchLogService.search(keyword, date, count, type);
    }
}
