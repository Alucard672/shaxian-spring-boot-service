package com.shaxian.biz.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

/**
 * 商品分享码工具类
 * 用于生成和解析商品分享码
 */
public class ProductShareCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProductShareCodeUtil.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int NONCE_LENGTH = 16;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分享码数据结构
     */
    public static class ShareCodeData {
        private Long productId;
        private Long tenantId;
        private String nonce;
        private Long timestamp;
        private String signature;

        public ShareCodeData() {
        }

        public ShareCodeData(Long productId, Long tenantId, String nonce, Long timestamp, String signature) {
            this.productId = productId;
            this.tenantId = tenantId;
            this.nonce = nonce;
            this.timestamp = timestamp;
            this.signature = signature;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public void setTenantId(Long tenantId) {
            this.tenantId = tenantId;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    /**
     * 生成16位随机字符串（字母+数字）
     */
    public static String generateNonce() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(NONCE_LENGTH);
        for (int i = 0; i < NONCE_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 计算HMAC-SHA256签名
     *
     * @param data      待签名的数据
     * @param secretKey 密钥
     * @return 签名字符串（十六进制）
     */
    public static String calculateSignature(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("计算签名失败", e);
            throw new RuntimeException("计算签名失败", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 生成分享码
     *
     * @param productId 商品ID
     * @param tenantId  租户ID
     * @param secretKey 签名密钥
     * @return Base64编码的分享码
     */
    public static String generateShareCode(Long productId, Long tenantId, String secretKey) {
        try {
            // 生成随机字符串
            String nonce = generateNonce();
            // 获取当前时间戳（秒级）
            long timestamp = System.currentTimeMillis() / 1000;

            // 构建签名字符串：productId + tenantId + nonce + timestamp
            String dataToSign = productId + String.valueOf(tenantId) + nonce + timestamp;
            // 计算签名
            String signature = calculateSignature(dataToSign, secretKey);

            // 构建分享码数据
            ShareCodeData shareCodeData = new ShareCodeData(productId, tenantId, nonce, timestamp, signature);

            // 序列化为JSON并Base64编码
            String json = objectMapper.writeValueAsString(shareCodeData);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("生成分享码失败", e);
            throw new RuntimeException("生成分享码失败", e);
        }
    }

    /**
     * 解析分享码
     *
     * @param shareCode Base64编码的分享码
     * @return 分享码数据
     * @throws IllegalArgumentException 如果分享码格式错误
     */
    public static ShareCodeData parseShareCode(String shareCode) {
        try {
            // Base64解码
            byte[] decodedBytes = Base64.getDecoder().decode(shareCode);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);

            // 解析JSON
            return objectMapper.readValue(json, ShareCodeData.class);
        } catch (Exception e) {
            logger.error("解析分享码失败: {}", shareCode, e);
            throw new IllegalArgumentException("分享码格式错误", e);
        }
    }

    /**
     * 验证签名
     *
     * @param data      分享码数据
     * @param secretKey 签名密钥
     * @return true表示签名正确，false表示签名错误
     */
    public static boolean verifySignature(ShareCodeData data, String secretKey) {
        try {
            // 构建签名字符串：productId + tenantId + nonce + timestamp
            String dataToSign = data.getProductId() + String.valueOf(data.getTenantId()) + data.getNonce() + data.getTimestamp();
            // 计算签名
            String calculatedSignature = calculateSignature(dataToSign, secretKey);
            // 比较签名
            return calculatedSignature.equals(data.getSignature());
        } catch (Exception e) {
            logger.error("验证签名失败", e);
            return false;
        }
    }
}
