package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.repository.ProductRepository;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.util.ProductShareCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商品分享码服务
 * 处理分享码生成和验证的业务逻辑
 */
@Service
public class ProductShareCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ProductShareCodeService.class);

    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    @Value("${product.share-code.secret-key}")
    private String secretKey;

    public ProductShareCodeService(ProductRepository productRepository, TenantRepository tenantRepository) {
        this.productRepository = productRepository;
        this.tenantRepository = tenantRepository;
    }

    /**
     * 生成分享码
     *
     * @param productId 商品ID
     * @param tenantId  租户ID
     * @return 分享码字符串
     * @throws IllegalArgumentException 如果商品不存在或租户不存在
     */
    public String generateShareCode(Long productId, Long tenantId) {
        // 验证商品是否存在
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("商品不存在");
        }

        // 验证租户是否存在
        if (!tenantRepository.existsById(tenantId)) {
            throw new IllegalArgumentException("租户不存在");
        }

        // 生成分享码
        return ProductShareCodeUtil.generateShareCode(productId, tenantId, secretKey);
    }

    /**
     * 验证分享码并返回商品ID和租户ID
     *
     * @param shareCode 分享码
     * @return 包含商品ID和租户ID的结果对象
     * @throws IllegalArgumentException 如果分享码格式错误、签名验证失败、商品不存在、租户不存在或租户已过期
     */
    public ShareCodeVerificationResult verifyAndGetProductId(String shareCode) {
        // 解析分享码
        ProductShareCodeUtil.ShareCodeData data;
        try {
            data = ProductShareCodeUtil.parseShareCode(shareCode);
        } catch (IllegalArgumentException e) {
            logger.warn("分享码格式错误: {}", shareCode);
            throw new IllegalArgumentException("分享码格式错误", e);
        }

        // 验证签名
        if (!ProductShareCodeUtil.verifySignature(data, secretKey)) {
            logger.warn("分享码签名验证失败: {}", shareCode);
            throw new IllegalArgumentException("分享码签名验证失败");
        }

        Long productId = data.getProductId();
        Long tenantId = data.getTenantId();

        // 验证商品是否存在
        if (!productRepository.existsById(productId)) {
            logger.warn("商品不存在: productId={}", productId);
            throw new IllegalArgumentException("商品不存在");
        }

        // 验证租户是否存在
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    logger.warn("租户不存在: tenantId={}", tenantId);
                    return new IllegalArgumentException("租户不存在");
                });

        // 验证租户是否在有效期内
        if (tenant.getExpiresAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (tenant.getExpiresAt().isBefore(now)) {
                logger.warn("租户已过期: tenantId={}, expiresAt={}", tenantId, tenant.getExpiresAt());
                throw new IllegalArgumentException("租户已过期");
            }
        }

        return new ShareCodeVerificationResult(productId, tenantId);
    }

    /**
     * 分享码验证结果
     */
    public static class ShareCodeVerificationResult {
        private final Long productId;
        private final Long tenantId;

        public ShareCodeVerificationResult(Long productId, Long tenantId) {
            this.productId = productId;
            this.tenantId = tenantId;
        }

        public Long getProductId() {
            return productId;
        }

        public Long getTenantId() {
            return tenantId;
        }
    }
}
