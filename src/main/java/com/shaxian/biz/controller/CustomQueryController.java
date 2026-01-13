package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.query.CustomQueryAppService;
import com.shaxian.biz.entity.CustomQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/biz/api/queries")
@Tag(name = "自定义查询", description = "自定义SQL查询管理接口")
public class CustomQueryController {

    private final CustomQueryAppService customQueryAppService;

    public CustomQueryController(CustomQueryAppService customQueryAppService) {
        this.customQueryAppService = customQueryAppService;
    }

    @GetMapping
    @Operation(summary = "获取自定义查询列表", description = "获取自定义查询列表，支持按模块筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取查询列表")
    })
    public ResponseEntity<ApiResponse<List<CustomQuery>>> getQueries(
            @Parameter(description = "模块名称") @RequestParam(required = false) String module) {
        List<CustomQuery> queries = customQueryAppService.listQueries(module);
        return ResponseEntity.ok(ApiResponse.ok(queries));
    }

    @PostMapping
    @Operation(summary = "创建自定义查询", description = "创建新的自定义SQL查询")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建自定义查询"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<ApiResponse<CustomQuery>> createQuery(@RequestBody Map<String, Object> request) {
        try {
            CustomQuery query = customQueryAppService.createQuery(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(query));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
