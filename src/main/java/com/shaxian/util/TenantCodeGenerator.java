package com.shaxian.util;

import com.shaxian.repository.TenantRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TenantCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    private final TenantRepository tenantRepository;
    private final Random random = new Random();

    public TenantCodeGenerator(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * 生成8位字母数字组合的全局唯一租户代码
     *
     * @return 唯一的租户代码
     * @throws IllegalStateException 如果重试10次后仍无法生成唯一代码
     */
    public String generateCode() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            if (!tenantRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("无法生成唯一的租户代码，请重试");
    }

    /**
     * 生成随机8位字母数字组合代码
     *
     * @return 随机代码
     */
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}

