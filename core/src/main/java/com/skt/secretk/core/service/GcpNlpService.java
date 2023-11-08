package com.skt.secretk.core.service;

import com.skt.secretk.core.model.GoogleNlpRequest;
import com.skt.secretk.core.model.GoogleNlpResponse;
import com.skt.secretk.core.model.GoogleNlpResponse.Entity;
import com.skt.secretk.core.model.GoogleNlpResult;
import com.skt.secretk.core.properties.KeyProperties;
import com.skt.secretk.core.util.CryptoUtils;
import com.skt.secretk.core.util.StreamUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GcpNlpService {

    private static final String API_URL = "https://language.googleapis.com/v2/documents:analyzeEntities?key=";

    private final WebClient webClient;

    private final KeyProperties keyProperties;

    public GoogleNlpResult query(String message) {

        try {
            File file = ResourceUtils.getFile("classpath:googleNlpApiKey.txt");
            Scanner scanner = new Scanner(file);
            String encryptedApiKey = scanner.nextLine();
            String apiKey = CryptoUtils.decrypt(encryptedApiKey, keyProperties.getCommonKey());
            scanner.close();

            // google api call
            GoogleNlpRequest.NlpRequestDocument requestDocument
                = GoogleNlpRequest.NlpRequestDocument.builder()
                                                     .type("PLAIN_TEXT")
                                                     .content(message)
                                                     .build();

            GoogleNlpRequest request = GoogleNlpRequest.builder()
                                                       .document(requestDocument)
                                                       .encodingType("UTF8")
                                                       .build();

            GoogleNlpResponse googleNlpResponse
                = webClient.post()
                           .uri(API_URL + apiKey)
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(Mono.just(request), GoogleNlpRequest.class)
                           .retrieve()
                           .bodyToMono(GoogleNlpResponse.class)
                           .block();

            if (googleNlpResponse == null) {
                return null;
            }

            List<String> totalEntityList = new ArrayList<>(); // 전체 단어
            List<String> peopleList = new ArrayList<>(); // 사람 단어
            List<String> entityList = new ArrayList<>(); // 사람 단어 제외한 나머지

            for (Entity entity : googleNlpResponse.getEntities()) {
                totalEntityList.add(entity.getName());

                if (StringUtils.equals(entity.getType(), "PERSON")) {
                    peopleList.add(entity.getName());
                } else {
                    entityList.add(entity.getName());
                }
            }

            return GoogleNlpResult.builder()
                                  .totalEntityList(totalEntityList)
                                  .peopleList(peopleList)
                                  .entityList(entityList)
                                  .build();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
