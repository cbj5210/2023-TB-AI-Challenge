package com.skt.secretk.core.controller;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {

        return "OK";
    }

    //TODO : remove
    @GetMapping("/test")
    public String test() {

        Firestore db = FirestoreClient.getFirestore();

        Map<Object, Object> item = new HashMap<Object, Object>();
        item.put("user", "2222222");
        item.put("type", "request");
        item.put("message", "insert test");
        item.put("createTime", "2023-08-19 01:01:01");
        item.put("solved", "false");
        db.collection("TBAI").add(item);

        return "OK";
    }
}
