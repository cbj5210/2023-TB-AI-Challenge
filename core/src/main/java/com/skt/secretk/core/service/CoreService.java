package com.skt.secretk.core.service;

import com.skt.secretk.core.model.Firebase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreService {

    private final GcpNlpService gcpNlpService;
    private final AlpacaService alpacaService;

    // TODO: 김영래님 부탁 드립니다.
    public Firebase execute(Firebase request) {

        String user = request.getUser(); // 사번 : 1111111
        String message = request.getMessage(); // 요청/응답 내용

        // 1. GCP NLP 질의하여 단어 추출
        List<String> entities = gcpNlpService.query(message);

        // 2. entities에 해당하는 key가 있으면 메세지 만들어서 리턴
        if (false) {
            return Firebase.builder()
                           .user(user)
                           .type("response")
                           .message(null)
                           .responseType(null) // text, url, file
                           .createTime(null) //yyyy-MM-dd HH:mm:ss
                           .build();
        }

        // 3. 준비된 대답이 아닌 경우 alpaca에 질의
        String alpacaResponse = alpacaService.query(message);

        return Firebase.builder()
                       .user(user)
                       .type("response")
                       .message(alpacaResponse)
                       .responseType("text")
                       .createTime(null) //yyyy-MM-dd HH:mm:ss
                       .build();
    }
}
