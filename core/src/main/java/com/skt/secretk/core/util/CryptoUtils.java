package com.skt.secretk.core.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.util.ResourceUtils;

public class CryptoUtils {
    private static final String RSA = "RSA";
    private static final String AES = "AES";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 2048;
    public static KeyPair createRSAKeypair() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            return keyPairGenerator.genKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey() {
        try {
            File file = new File("/home/service/private.pem");
            byte[] bytes = Files.readAllBytes(file.toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  PublicKey getPublicKey() {
        try {
            File file = ResourceUtils.getFile("classpath:public.pem");
            byte[] bytes = Files.readAllBytes(file.toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 암호화 : 공개키로 진행
     */
    public static String rsaEncrypt(String plainText, PublicKey publicKey) {
        try {
            // 만들어진 공개키 객체로 암호화 설정
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 복호화 : 개인키로 진행
     */
    public static String rsaDecrypt(String encryptedText, PrivateKey privateKey) {
        try {
            // 만들어진 공개키 객체로 복호화 설정
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호문을 평문화하는 과정
            byte[] encryptedBytes =  Base64.getDecoder().decode(encryptedText.getBytes());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * AES로 평문 암호화
     */
    public static String aesEncrypt(String plainText, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), AES);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * AES로 평문 복호화
     */
    public static String aesDecrypt(String encryptedText, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), AES);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedText, String commonKey) {
        PrivateKey privateKey = CryptoUtils.getPrivateKey();
        return aesDecrypt(encryptedText, CryptoUtils.rsaDecrypt(commonKey, privateKey));
    }
}
