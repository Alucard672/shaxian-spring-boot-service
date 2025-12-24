package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.crm.dto.request.CreateCrmProductRequest;
import com.shaxian.crm.dto.request.CrmProductQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmProductRequest;
import com.shaxian.crm.entity.CrmProduct;
import com.shaxian.crm.service.CrmProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrmProductAppService {

    private final CrmProductService crmProductService;

    public CrmProductAppService(CrmProductService crmProductService) {
        this.crmProductService = crmProductService;
    }

    public List<CrmProduct> listProducts() {
        return crmProductService.getAll();
    }

    public PageResult<CrmProduct> queryProducts(CrmProductQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<CrmProduct> page = crmProductService.queryProducts(
                request.getName(),
                request.getCode(),
                request.getStatus(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    public Optional<CrmProduct> findProduct(Long id) {
        return crmProductService.getById(id);
    }

    public CrmProduct createProduct(CreateCrmProductRequest request) {
        CrmProduct product = new CrmProduct();
        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setUnitPrice(request.getUnitPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setProductValue(request.getProductValue());
        product.setLicenseCount(request.getLicenseCount());
        if (request.getStatus() != null) {
            product.setStatus(CrmProduct.ProductStatus.valueOf(request.getStatus()));
        }
        product.setDescription(request.getDescription());
        return crmProductService.create(product);
    }

    public CrmProduct updateProduct(Long id, UpdateCrmProductRequest request) {
        CrmProduct product = new CrmProduct();
        if (request.getName() != null) product.setName(request.getName());
        if (request.getCode() != null) product.setCode(request.getCode());
        if (request.getUnitPrice() != null) product.setUnitPrice(request.getUnitPrice());
        if (request.getDiscountPrice() != null) product.setDiscountPrice(request.getDiscountPrice());
        if (request.getProductValue() != null) product.setProductValue(request.getProductValue());
        if (request.getLicenseCount() != null) product.setLicenseCount(request.getLicenseCount());
        if (request.getStatus() != null) {
            product.setStatus(CrmProduct.ProductStatus.valueOf(request.getStatus()));
        }
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        return crmProductService.update(id, product);
    }

    public CrmProduct activateProduct(Long id) {
        return crmProductService.activate(id);
    }

    public CrmProduct deactivateProduct(Long id) {
        return crmProductService.deactivate(id);
    }
}

