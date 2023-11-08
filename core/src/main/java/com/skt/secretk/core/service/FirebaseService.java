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

    private static final long TIMEOUT_SECONDS = 600;

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

        db.collection("TBAI")
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
                              String documentId = dc.getDocument().getId();
                              System.out.println("add documentId : " + documentId);

                              Map<String, Object> data = dc.getDocument().getData();
                              String user = (String) data.get("user");
                              String type = (String) data.get("type");
                              String message = (String) data.get("message");
                              //String createTime = (String) data.get("createTime");

                              // 무언가 처리

                              // 기존 request의 solved를 변경
                              Map<String, Object> updateSolved = new HashMap<>(data);
                              updateSolved.put("solved", "true");

                              db.collection("TBAI").document(documentId).update(updateSolved);

                              // 응답을 추가
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
