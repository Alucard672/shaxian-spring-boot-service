package com.shaxian.biz.service.shortcode;

import com.shaxian.biz.entity.ShortCode;
import com.shaxian.biz.repository.ShortCodeRepository;
import com.shaxian.biz.util.ShortCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 短码服务
 * 负责短码的生成和查询
 */
@Service
public class ShortCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ShortCodeService.class);
    private static final int MAX_RETRY_COUNT = 5;

    private final ShortCodeRepository shortCodeRepository;

    public ShortCodeService(ShortCodeRepository shortCodeRepository) {
        this.shortCodeRepository = shortCodeRepository;
    }

    /**
     * 为原始分享码生成短码
     * 如果生成的短码已存在，会重试生成（最多5次）
     *
     * @param originalCode 原始分享码
     * @return 短码
     * @throws RuntimeException 如果重试5次后仍然碰撞
     */
    @Transactional
    public String generateShortCode(String originalCode) {
        int retryCount = 0;
        String shortCode;

        while (retryCount < MAX_RETRY_COUNT) {
            // 生成随机短码
            shortCode = ShortCodeUtil.generateRandomShortCode();

            // 检查是否已存在
            if (!shortCodeRepository.existsByShortCode(shortCode)) {
                // 不存在，保存到数据库
                ShortCode entity = new ShortCode();
                entity.setShortCode(shortCode);
                entity.setOriginalCode(originalCode);
                shortCodeRepository.save(entity);

                logger.debug("成功生成短码: {} -> {}", shortCode, originalCode);
                return shortCode;
            }

            // 存在，重试
            retryCount++;
            logger.warn("短码碰撞，重试第 {} 次: {}", retryCount, shortCode);
        }

        // 重试5次后仍然失败
        throw new RuntimeException("生成短码失败，重试 " + MAX_RETRY_COUNT + " 次后仍然碰撞");
    }

    /**
     * 根据短码获取原始分享码
     *
     * @param shortCode 短码
     * @return 原始分享码，如果短码不存在则返回空
     */
    public Optional<String> getOriginalCode(String shortCode) {
        return shortCodeRepository.findByShortCode(shortCode)
                .map(ShortCode::getOriginalCode);
    }
}
