package com.shaxian.appservice.system;

import com.shaxian.entity.SystemParams;
import com.shaxian.service.settings.SystemParamsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SystemParamsAppService {

    private final SystemParamsService systemParamsService;

    public SystemParamsAppService(SystemParamsService systemParamsService) {
        this.systemParamsService = systemParamsService;
    }

    public SystemParams getSystemParams() {
        return systemParamsService.getSystemParams();
    }

    public SystemParams updateSystemParams(Map<String, Object> request) {
        return systemParamsService.updateSystemParams(request);
    }
}
