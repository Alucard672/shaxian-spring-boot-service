package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.repository.ProductRepository;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.service.shortcode.ShortCodeService;
import com.shaxian.biz.util.ProductShareCodeUtil;
import com.shaxian.biz.util.TenantContext;
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
    private final ShortCodeService shortCodeService;

    @Value("${product.share-code.secret-key}")
    private String secretKey;

    public ProductShareCodeService(ProductRepository productRepository, TenantRepository tenantRepository, ShortCodeService shortCodeService) {
        this.productRepository = productRepository;
        this.tenantRepository = tenantRepository;
        this.shortCodeService = shortCodeService;
    }

    /**
     * 生成分享码（返回短码）
     *
     * @param productId 商品ID
     * @param tenantId  租户ID
     * @return 短码字符串
     * @throws IllegalArgumentException 如果商品不存在或租户不存在
     */
    public String generateShareCode(Long productId, Long tenantId) {
        logger.info("生成分享码: productId={}, tenantId={}", productId, tenantId);
        
        // 验证商品是否存在（显式使用tenantId，确保在多租户环境下正确验证）
        boolean exists = productRepository.existsByIdAndTenantId(productId, tenantId);
        logger.info("商品存在性检查结果: productId={}, tenantId={}, exists={}", productId, tenantId, exists);
        
        if (!exists) {
            logger.warn("商品不存在: productId={}, tenantId={}", productId, tenantId);
            throw new IllegalArgumentException("商品不存在");
        }

        // 验证租户是否存在
        if (!tenantRepository.existsById(tenantId)) {
            throw new IllegalArgumentException("租户不存在");
        }

        // 生成原始分享码
        String originalShareCode = ProductShareCodeUtil.generateShareCode(productId, tenantId, secretKey);

        // 生成短码并返回
        return shortCodeService.generateShortCode(originalShareCode);
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

        logger.info("开始验证商品存在性: productId={}, tenantId={}", productId, tenantId);
        
        // 设置租户上下文，让Hibernate多租户机制使用正确的tenantId
        // 因为分享码场景没有会话信息，需要手动设置tenantId
        TenantContext.setTenantId(tenantId);
        
        try {
            // 验证商品是否存在（显式使用tenantId，同时设置TenantContext让多租户机制也使用正确的tenantId）
            boolean exists = productRepository.existsByIdAndTenantId(productId, tenantId);
            logger.info("商品存在性检查结果: productId={}, tenantId={}, exists={}", productId, tenantId, exists);
            
            if (!exists) {
                logger.warn("商品不存在: productId={}, tenantId={}", productId, tenantId);
                // 尝试直接查询看看是否能找到
                var productOpt = productRepository.findByIdAndTenantId(productId, tenantId);
                logger.warn("直接查询结果: productId={}, tenantId={}, found={}", productId, tenantId, productOpt.isPresent());
                throw new IllegalArgumentException("商品不存在");
            }
        } finally {
            // 清理租户上下文
            TenantContext.clear();
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
