package com.skt.secretk.core.service;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentChange.Type;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
import com.skt.secretk.core.model.Firebase;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private static final String FIREBASE_DB_NAME = "TBAI";
    private static final long TIMEOUT_SECONDS = 600;

    private final CoreService coreService;

    @PostConstruct
    public void initialize(){
        try {
            listenForMultiple();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    List<DocumentChange> listenForMultiple() throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        final SettableApiFuture<List<DocumentChange>> future = SettableApiFuture.create();

        db.collection(FIREBASE_DB_NAME)
          .whereEqualTo("type", "request")
          .whereEqualTo("solved", "false")
          .addSnapshotListener(
              new EventListener<QuerySnapshot>() {
                  @Override
                  public void onEvent(
                      @Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
                      if (e != null) {
                          System.err.println("Listen failed:" + e);
                          return;
                      }

                      for (DocumentChange dc : snapshots.getDocumentChanges()) {
                          if (dc.getType() == Type.ADDED) {
                              // request data
                              Map<String, Object> data = dc.getDocument().getData();

                              // 기존 request의 solved를 변경
                              String documentId = dc.getDocument().getId();
                              Map<String, Object> updateSolved = new HashMap<>(data);
                              updateSolved.put("solved", "true");
                              db.collection(FIREBASE_DB_NAME).document(documentId).update(updateSolved);

                              // core 처리
                              Firebase request = Firebase.builder()
                                                         .user((String) data.get("user"))
                                                         .message((String) data.get("message"))
                                                         .build();

                              Firebase response = coreService.execute(request);

                              // 응답을 추가
                              Map<String, Object> newData = new HashMap<>();
                              newData.put("user", response.getUser());
                              newData.put("type", response.getType());
                              newData.put("message", response.getMessage());
                              newData.put("responseType", response.getResponseType());
                              newData.put("createTime", response.getCreateTime());
                              db.collection(FIREBASE_DB_NAME).add(newData);
                          }
                      }

                      if (!future.isDone()) {
                          future.set(snapshots.getDocumentChanges());
                      }
                  }
              });

        return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
