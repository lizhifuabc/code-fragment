package io.github.lizhifuabc.web.security;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密解密工具类
 * 用于敏感数据的加密传输，如密码、银行卡号等
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class RSAUtils {

    /**
     * RSA算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    
    /**
     * RSA密钥长度
     */
    private static final int KEY_SIZE = 2048;

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取公钥字符串
     *
     * @param keyPair 密钥对
     * @return Base64编码的公钥
     */
    public static String getPublicKeyString(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取私钥字符串
     *
     * @param keyPair 密钥对
     * @return Base64编码的私钥
     */
    public static String getPrivateKeyString(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 使用公钥加密
     *
     * @param data 待加密数据
     * @param publicKeyString Base64编码的公钥
     * @return Base64编码的加密数据
     */
    public static String encrypt(String data, String publicKeyString) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    /**
     * 使用私钥解密
     *
     * @param encryptedData Base64编码的加密数据
     * @param privateKeyString Base64编码的私钥
     * @return 解密后的数据
     */
    public static String decrypt(String encryptedData, String privateKeyString) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }
}