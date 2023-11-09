package com.skt.secretk.core.service;

import com.skt.secretk.core.model.GoogleNlpRequest;
import com.skt.secretk.core.model.GoogleNlpResponse;
import com.skt.secretk.core.model.GoogleNlpResponse.Entity;
import com.skt.secretk.core.util.StreamUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
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

    public List<String> query(String message) {

        try {
            File file = ResourceUtils.getFile("classpath:googleNlpApiKey.txt");
            Scanner scanner = new Scanner(file);
            String apiKey = scanner.nextLine();
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

            List<GoogleNlpResponse.Entity> entityList
                = Optional.ofNullable(googleNlpResponse)
                          .map(GoogleNlpResponse::getEntities)
                          .orElse(null);

            return StreamUtils.ofNullable(entityList)
                              .map(Entity::getName)
                              .toList();
        } catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }
}
