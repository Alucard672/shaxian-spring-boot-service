package com.shaxian.biz.appservice.store;

import com.shaxian.biz.entity.StoreInfo;
import com.shaxian.biz.service.settings.StoreInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StoreInfoAppService {

    private final StoreInfoService storeInfoService;

    public StoreInfoAppService(StoreInfoService storeInfoService) {
        this.storeInfoService = storeInfoService;
    }

    public StoreInfo getStoreInfo() {
        return storeInfoService.getStoreInfo();
    }

    public StoreInfo updateStoreInfo(Map<String, Object> request) {
        return storeInfoService.updateStoreInfo(request);
    }
}
