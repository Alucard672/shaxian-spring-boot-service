package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.store.StoreInfoAppService;
import com.shaxian.biz.entity.StoreInfo;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/biz/api/store")
@Tag(name = "门店信息", description = "门店信息管理接口")
public class StoreInfoController {

    private final StoreInfoAppService storeInfoAppService;

    public StoreInfoController(StoreInfoAppService storeInfoAppService) {
        this.storeInfoAppService = storeInfoAppService;
    }

    @GetMapping
    @Operation(summary = "获取门店信息", description = "获取当前门店信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取门店信息")
    })
    public ResponseEntity<ApiResponse<StoreInfo>> getStoreInfo() {
        StoreInfo storeInfo = storeInfoAppService.getStoreInfo();
        return ResponseEntity.ok(ApiResponse.ok(storeInfo));
    }

    @PutMapping
    @Operation(summary = "更新门店信息", description = "更新门店信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新门店信息")
    })
    public ResponseEntity<ApiResponse<StoreInfo>> updateStoreInfo(@RequestBody Map<String, Object> request) {
        StoreInfo storeInfo = storeInfoAppService.updateStoreInfo(request);
        return ResponseEntity.ok(ApiResponse.ok(storeInfo));
    }
}
