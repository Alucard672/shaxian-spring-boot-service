package com.shaxian.crm.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 * 提供SHA256加密功能
 */
public class PasswordUtil {

    /**
     * 对密码进行SHA256加密
     * @param password 原始密码（可能是明文，也可能是已经过一次SHA256的密码）
     * @return 小写十六进制字符串（64个字符）
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * 对密码进行双重SHA256加密
     * 用于创建用户时，对原始密码进行双重加密后存储
     * @param plainPassword 原始明文密码
     * @return 双重SHA256加密后的密码
     */
    public static String hashPasswordTwice(String plainPassword) {
        // 第一次SHA256
        String firstHash = hashPassword(plainPassword);
        // 第二次SHA256
        return hashPassword(firstHash);
    }
}

