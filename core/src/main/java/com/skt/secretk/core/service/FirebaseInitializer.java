package com.skt.secretk.core.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class FirebaseInitializer {

    @PostConstruct
    public void initialize(){
        try{
            File file = ResourceUtils.getFile("classpath:serviceAccountKey.json");
            InputStream serviceAccount = new FileInputStream(file);
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
