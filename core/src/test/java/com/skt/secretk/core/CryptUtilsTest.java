package com.skt.secretk.core;

import com.skt.secretk.core.properties.KeyProperties;
import com.skt.secretk.core.util.CryptoUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@Slf4j
@SpringBootTest

public class CryptUtilsTest {
    @Autowired
    private KeyProperties keyProperties;

    @Disabled
    @Test
    public void createKeyFile() {
        KeyPair keyPair = CryptoUtils.createRSAKeypair();
        try(FileOutputStream fos = new FileOutputStream("src/main/resources/public.pem")) {
            fos.write(keyPair.getPublic().getEncoded());
        } catch (Exception e) {
            log.error("create key error.", e);
        }

        try(FileOutputStream fos = new FileOutputStream("src/main/resources/private.pem")) {
            fos.write(keyPair.getPrivate().getEncoded());
        } catch (Exception e) {
            log.error("create key error.", e);
        }

    }

    @Disabled
    @Test
    public void encryptAesKey() {
        String plainText = "";
        System.out.println("=== 원문: " + plainText);

        PublicKey publicKey = CryptoUtils.getPublicKey();
        String encryptedText = CryptoUtils.rsaEncrypt(plainText, publicKey);
        System.out.println("=== 암호화: " + encryptedText);

        PrivateKey privateKey = CryptoUtils.getPrivateKey();
        String decryptedText = CryptoUtils.rsaDecrypt(encryptedText, privateKey);
        System.out.println("=== 복호화: " + decryptedText);
    }

    @Test
    public void encryptServiceAccountKey() {
        String key = keyProperties.getCommonKey();

        String serviceAccountKeyText = "";

        System.out.println("=== 원문: " + serviceAccountKeyText);
        PrivateKey privateKey = CryptoUtils.getPrivateKey();

        String encryptedText = CryptoUtils.aesEncrypt(serviceAccountKeyText, CryptoUtils.rsaDecrypt(key, privateKey));
        System.out.println("=== 암호화: " + encryptedText);

        String decryptedText = CryptoUtils.aesDecrypt(encryptedText, CryptoUtils.rsaDecrypt(key, privateKey));
        System.out.println("=== 복호화: " + decryptedText);
    }

    @Test
    public void encryptGoogleNlpApiKey() {
        String key = keyProperties.getCommonKey();

        String googleNlpApiKeyText = "";

        System.out.println("=== 원문: " + googleNlpApiKeyText);
        PrivateKey privateKey = CryptoUtils.getPrivateKey();

        String encryptedText = CryptoUtils.aesEncrypt(googleNlpApiKeyText, CryptoUtils.rsaDecrypt(key, privateKey));
        System.out.println("=== 암호화: " + encryptedText);

        String decryptedText = CryptoUtils.aesDecrypt(encryptedText, CryptoUtils.rsaDecrypt(key, privateKey));
        System.out.println("=== 복호화: " + decryptedText);
    }

    @Test
    public void decryptServiceAccountKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:serviceAccountKey.txt");
        String apiKey = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        String decryptedText = CryptoUtils.decrypt(apiKey, keyProperties.getCommonKey());
        System.out.println("=== 복호화: " + decryptedText);
    }

    @Test
    public void decryptGoogleNlpApiKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:googleNlpApiKey.txt");
        Scanner scanner = new Scanner(file);
        String apiKey = scanner.nextLine();

        String decryptedText = CryptoUtils.decrypt(apiKey, keyProperties.getCommonKey());
        System.out.println("=== 복호화: " + decryptedText);
    }

}
