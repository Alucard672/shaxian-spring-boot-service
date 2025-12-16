package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.store.StoreInfoAppService;
import com.shaxian.entity.StoreInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/store")
public class StoreInfoController {

    private final StoreInfoAppService storeInfoAppService;

    public StoreInfoController(StoreInfoAppService storeInfoAppService) {
        this.storeInfoAppService = storeInfoAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StoreInfo>> getStoreInfo() {
        StoreInfo storeInfo = storeInfoAppService.getStoreInfo();
        return ResponseEntity.ok(ApiResponse.ok(storeInfo));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<StoreInfo>> updateStoreInfo(@RequestBody Map<String, Object> request) {
        StoreInfo storeInfo = storeInfoAppService.updateStoreInfo(request);
        return ResponseEntity.ok(ApiResponse.ok(storeInfo));
    }
}
