package com.shaxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaxian.entity.PrintTemplate;
import com.shaxian.repository.PrintTemplateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@Tag(name = "模板管理", description = "打印模板管理接口")
public class TemplateController {
    private final PrintTemplateRepository printTemplateRepository;
    private final ObjectMapper objectMapper;

    public TemplateController(
            PrintTemplateRepository printTemplateRepository,
            ObjectMapper objectMapper) {
        this.printTemplateRepository = printTemplateRepository;
        this.objectMapper = objectMapper;
    }


    @GetMapping
    @Operation(summary = "获取打印模板列表", description = "获取打印模板列表，支持按文档类型筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取打印模板列表")
    })
    public ResponseEntity<List<PrintTemplate>> getAllTemplates(
            @Parameter(description = "文档类型") @RequestParam(required = false) String documentType) {
        if (documentType != null) {
            return ResponseEntity.ok(printTemplateRepository.findByDocumentType(documentType));
        }
        return ResponseEntity.ok(printTemplateRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取打印模板详情", description = "根据ID获取打印模板信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取打印模板信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "打印模板不存在")
    })
    public ResponseEntity<PrintTemplate> getTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id) {
        return printTemplateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建打印模板", description = "创建新的打印模板")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建打印模板"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> createTemplate(@RequestBody Map<String, Object> request) {
        try {
            PrintTemplate template = new PrintTemplate();
            template.setName((String) request.get("name"));
            if (request.containsKey("type")) {
                template.setType(PrintTemplate.TemplateType.valueOf((String) request.get("type")));
            }
            if (request.containsKey("description")) template.setDescription((String) request.get("description"));
            if (request.containsKey("isDefault")) template.setIsDefault((Boolean) request.get("isDefault"));
            if (request.containsKey("documentType")) {
                template.setDocumentType(PrintTemplate.DocumentType.valueOf((String) request.get("documentType")));
            }
            if (request.containsKey("pageSettings")) {
                template.setPageSettings(objectMapper.writeValueAsString(request.get("pageSettings")));
            }
            if (request.containsKey("titleSettings")) {
                template.setTitleSettings(objectMapper.writeValueAsString(request.get("titleSettings")));
            }
            if (request.containsKey("basicInfoFields")) {
                template.setBasicInfoFields(objectMapper.writeValueAsString(request.get("basicInfoFields")));
            }
            if (request.containsKey("productFields")) {
                template.setProductFields(objectMapper.writeValueAsString(request.get("productFields")));
            }
            if (request.containsKey("summaryFields")) {
                template.setSummaryFields(objectMapper.writeValueAsString(request.get("summaryFields")));
            }
            if (request.containsKey("otherElements")) {
                template.setOtherElements(objectMapper.writeValueAsString(request.get("otherElements")));
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(printTemplateRepository.save(template));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新打印模板", description = "更新打印模板信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新打印模板"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> updateTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            PrintTemplate template = printTemplateRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("模板不存在"));
            
            if (request.containsKey("name")) template.setName((String) request.get("name"));
            if (request.containsKey("type")) {
                template.setType(PrintTemplate.TemplateType.valueOf((String) request.get("type")));
            }
            if (request.containsKey("description")) template.setDescription((String) request.get("description"));
            if (request.containsKey("isDefault")) template.setIsDefault((Boolean) request.get("isDefault"));
            if (request.containsKey("documentType")) {
                template.setDocumentType(PrintTemplate.DocumentType.valueOf((String) request.get("documentType")));
            }
            if (request.containsKey("pageSettings")) {
                template.setPageSettings(objectMapper.writeValueAsString(request.get("pageSettings")));
            }
            if (request.containsKey("titleSettings")) {
                template.setTitleSettings(objectMapper.writeValueAsString(request.get("titleSettings")));
            }
            if (request.containsKey("basicInfoFields")) {
                template.setBasicInfoFields(objectMapper.writeValueAsString(request.get("basicInfoFields")));
            }
            if (request.containsKey("productFields")) {
                template.setProductFields(objectMapper.writeValueAsString(request.get("productFields")));
            }
            if (request.containsKey("summaryFields")) {
                template.setSummaryFields(objectMapper.writeValueAsString(request.get("summaryFields")));
            }
            if (request.containsKey("otherElements")) {
                template.setOtherElements(objectMapper.writeValueAsString(request.get("otherElements")));
            }
            
            return ResponseEntity.ok(printTemplateRepository.save(template));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除打印模板", description = "删除指定打印模板")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除打印模板"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "打印模板不存在")
    })
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id) {
        if (!printTemplateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        printTemplateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/usage")
    @Operation(summary = "增加模板使用次数", description = "记录模板使用次数")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新使用次数"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "打印模板不存在")
    })
    public ResponseEntity<?> incrementUsage(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id) {
        Optional<PrintTemplate> templateOpt = printTemplateRepository.findById(id);
        if (templateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PrintTemplate template = templateOpt.get();
        template.setUsageCount(template.getUsageCount() + 1);
        printTemplateRepository.save(template);
        return ResponseEntity.ok(Map.of("usageCount", template.getUsageCount()));
    }
}

