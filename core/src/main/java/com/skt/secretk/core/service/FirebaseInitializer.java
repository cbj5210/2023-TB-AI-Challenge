package com.skt.secretk.core.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.skt.secretk.core.properties.KeyProperties;
import com.skt.secretk.core.util.CryptoUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@RequiredArgsConstructor
public class FirebaseInitializer {
    private final KeyProperties keyProperties;

    @PostConstruct
    public void initialize(){
        try{
            File file = ResourceUtils.getFile("classpath:serviceAccountKey.txt");
            String encryptedKey = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            String key = CryptoUtils.decrypt(encryptedKey, keyProperties.getCommonKey());
            InputStream serviceAccount = new ByteArrayInputStream(key.getBytes());
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
            FirebaseApp.initializeApp(options);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
