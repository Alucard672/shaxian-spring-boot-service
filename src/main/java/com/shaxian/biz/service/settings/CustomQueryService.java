package com.shaxian.biz.service.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaxian.biz.entity.CustomQuery;
import com.shaxian.biz.repository.CustomQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomQueryService {

    private final CustomQueryRepository customQueryRepository;
    private final ObjectMapper objectMapper;

    public CustomQueryService(CustomQueryRepository customQueryRepository, ObjectMapper objectMapper) {
        this.customQueryRepository = customQueryRepository;
        this.objectMapper = objectMapper;
    }

    public List<CustomQuery> getAll(String module) {
        if (module != null) {
            return customQueryRepository.findByModule(module);
        }
        return customQueryRepository.findAll();
    }

    @Transactional
    public CustomQuery create(java.util.Map<String, Object> request) throws JsonProcessingException {
        CustomQuery query = new CustomQuery();
        query.setName((String) request.get("name"));
        query.setModule((String) request.get("module"));
        if (request.containsKey("conditions")) {
            query.setConditions(objectMapper.writeValueAsString(request.get("conditions")));
        }
        return customQueryRepository.save(query);
    }
}
