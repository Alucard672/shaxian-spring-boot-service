package com.shaxian.biz.service.settings;

import com.shaxian.biz.entity.SystemParams;
import com.shaxian.biz.repository.SystemParamsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class SystemParamsService {

    private final SystemParamsRepository systemParamsRepository;

    public SystemParamsService(SystemParamsRepository systemParamsRepository) {
        this.systemParamsRepository = systemParamsRepository;
    }

    public SystemParams getSystemParams() {
        Optional<SystemParams> params = systemParamsRepository.findFirstByOrderByIdAsc();
        if (params.isEmpty()) {
            SystemParams newParams = new SystemParams();
            return systemParamsRepository.save(newParams);
        }
        return params.get();
    }

    @Transactional
    public SystemParams updateSystemParams(Map<String, Object> request) {
        Optional<SystemParams> existing = systemParamsRepository.findFirstByOrderByIdAsc();
        SystemParams params;

        if (existing.isPresent()) {
            params = existing.get();
        } else {
            params = new SystemParams();
        }

        if (request.containsKey("enableDyeingProcess")) {
            params.setEnableDyeingProcess((Boolean) request.get("enableDyeingProcess"));
        }
        if (request.containsKey("allowNegativeStock")) {
            params.setAllowNegativeStock((Boolean) request.get("allowNegativeStock"));
        }

        return systemParamsRepository.save(params);
    }
}
