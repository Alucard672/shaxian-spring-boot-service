package com.shaxian.biz.util;

import java.util.Random;

/**
 * 短码工具类
 * 用于生成随机短码
 */
public class ShortCodeUtil {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final Random random = new Random();

    /**
     * 生成6位随机短码
     * 使用大小写字母+数字（a-z, A-Z, 0-9），共62个字符
     *
     * @return 6位随机短码
     */
    public static String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(index));
        }
        return sb.toString();
    }
}
