package com.shaxian.biz.appservice.query;

import com.shaxian.biz.entity.CustomQuery;
import com.shaxian.biz.service.settings.CustomQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomQueryAppService {

    private final CustomQueryService customQueryService;

    public CustomQueryAppService(CustomQueryService customQueryService) {
        this.customQueryService = customQueryService;
    }

    public List<CustomQuery> listQueries(String module) {
        return customQueryService.getAll(module);
    }

    public CustomQuery createQuery(Map<String, Object> request) throws Exception {
        return customQueryService.create(request);
    }
}
