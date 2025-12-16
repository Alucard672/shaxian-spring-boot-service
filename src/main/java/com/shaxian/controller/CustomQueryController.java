package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.query.CustomQueryAppService;
import com.shaxian.entity.CustomQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queries")
public class CustomQueryController {

    private final CustomQueryAppService customQueryAppService;

    public CustomQueryController(CustomQueryAppService customQueryAppService) {
        this.customQueryAppService = customQueryAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomQuery>>> getQueries(@RequestParam(required = false) String module) {
        List<CustomQuery> queries = customQueryAppService.listQueries(module);
        return ResponseEntity.ok(ApiResponse.ok(queries));
    }

    @PostMapping
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
