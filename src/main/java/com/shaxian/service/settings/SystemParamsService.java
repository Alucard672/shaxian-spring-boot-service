package com.shaxian.service.settings;

import com.shaxian.entity.SystemParams;
import com.shaxian.repository.SystemParamsRepository;
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

        return systemParamsRepository.save(params);
    }
}
