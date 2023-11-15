package com.skt.secretk.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Lists;
import com.google.firebase.database.utilities.Pair;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Alpaca2Test {
    private static final String CONVERSATION_URL = "https://chat.koalpaca.com/conversation";
    //    private static final String CONVERSATION_URL = "http://localhost:8081/alpaca";
    private static final int TIMEOUT = 10000;
    private WebClient webClient;

    @Test
    public void dddfd() {
        System.out.println(System.getProperty("user.home"));
    }
    @Test
    public void test() {
        webClient = createWebClient();

        String inputMessage = "점심 메뉴 추천해줘";
        ConversationResponse conversationResponse = getConversationV2();
//        String conversationId = "654b2e4f9276fd4020bb33a9";
//        String conversationId = "stream-flux";
        System.out.println("conversationId : " + conversationResponse.getConversationId());
        System.out.println("cookie : " + conversationResponse.getCookieValue());

        System.out.println("=== start ===");
        executeV5(conversationResponse.getConversationId(), inputMessage);
        System.out.println("=== done ===");


    }

    private void executeV3(String conversationId, String question) {
        WebClient client = WebClient.create(CONVERSATION_URL);

        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {};

        client.post()
              .uri("/654b2e4f9276fd4020bb33a9")
              .accept(MediaType.TEXT_EVENT_STREAM)
//              .body(Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
              .retrieve()
              .bodyToFlux(type)
              .map(ServerSentEvent::data)
              .onBackpressureBuffer()
              .publishOn(Schedulers.single())
              .blockLast();

    }
    private void executeV2(String conversationId, String question) {
        WebClient client = WebClient.create(CONVERSATION_URL);

        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {};

        Flux<ServerSentEvent<String>> eventStream = client.post()
                                                          .uri("/" + conversationId)
                                                          .accept(MediaType.TEXT_EVENT_STREAM)
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .body(Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
                                                          .retrieve()
                                                          .bodyToFlux(type);

        eventStream.subscribe(
            content -> {
                System.out.println("data : " + content.data());
            },
            error -> System.out.println("Error receiving SSE: " + error),
            () -> System.out.println("Completed!!!"));

    }

    private void executeV5(String conversationId, String question) {
        WebClient client = WebClient.create(CONVERSATION_URL);

        ParameterizedTypeReference<KoAlpacaResponse> type = new ParameterizedTypeReference<>() {};

        KoAlpacaResponse eventStream = client.post()
                                   .uri("/" + conversationId)

                                   .accept(MediaType.TEXT_EVENT_STREAM)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .cookie("hf-chat", "d21f5fae-146b-486c-8235-2fcba033ecf1")
                                   .body(Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
                                   .retrieve()
                                   .bodyToFlux(type)
                                   .blockLast();
        System.out.println(eventStream.generated_text);
//        eventStream.subscribe(
//            content -> {
//                System.out.println("data : " + content);
//            },
//            error -> System.out.println("Error receiving SSE: " + error),
//            () -> System.out.println("Completed!!!"));

    }

    private void executeV4(String conversationId, String question) {
        WebClient client = WebClient.create(CONVERSATION_URL);

        ParameterizedTypeReference<ServerSentEvent<JsonNode>> type = new ParameterizedTypeReference<>() {};

        Flux<ServerSentEvent<JsonNode>> eventStream = client.post()
                                                            .uri("/654b2e4f9276fd4020bb33a9")
                                                            .accept(MediaType.TEXT_EVENT_STREAM)
                                                            .body(Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
                                                            .exchange()
                                                            .flatMapMany(response -> response.bodyToFlux(type));

        eventStream.subscribe(System.out::println,  // (9)
                              Throwable::printStackTrace);

    }

    private void execute(String conversationId, String question) {
        KoAlpacaResponse response = webClient.post()
                                             .uri(CONVERSATION_URL + "/" + conversationId)
                                             .header("Origin", "https://chat.koalpaca.com")
                                             .header("Referer", CONVERSATION_URL + "/" + conversationId)
                                             .accept(MediaType.APPLICATION_STREAM_JSON)
                                             .cookie("hf-chat", "376cc7e5-41cc-4e5b-ac16-e3fcf5c5cf8b")
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(
                                                 Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
                                             .exchangeToFlux(clientResponse -> {
                                                 return clientResponse.bodyToFlux(KoAlpacaResponse.class);
                                             })
                                             .onErrorResume(
                                                 e-> {
                                                     System.out.println("e log : " + e.getMessage());
                                                     return Flux.empty();
                                                 })
                                             .log().blockLast();

        Optional.ofNullable(response).map(KoAlpacaResponse::getGenerated_text).orElseThrow(() -> new RuntimeException("ERROR"));
    }

    private WebClient createWebClient() {
        HttpClient httpClient = HttpClient.create()
                                          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                                          .responseTimeout(Duration.ofMillis(TIMEOUT))
                                          .followRedirect(true)
                                          .doOnConnected(conn ->
                                                             conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                                                                 .addHandlerLast(
                                                                     new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .defaultHeader("user-agent",
                                       "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                        .build();
    }

    private String getConversation() {
        ConversationResponse response = webClient.post()
                                                 .uri(CONVERSATION_URL)
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .retrieve()
                                                 .bodyToMono(ConversationResponse.class)
                                                 .block();


        return response.conversationId;
    }

    private ConversationResponse getConversationV2() {
        ConversationResponse conversationResponse = webClient.post()
            .uri(CONVERSATION_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> {

                String cookieValue = MapUtils.getObject(response.cookies(), "hf-cha1t", Lists.newArrayList()).stream().findFirst().map(HttpCookie::getValue).orElse(null);
                return response.bodyToMono(ConversationResponse.class).map(it -> it.withCookieValue(cookieValue));
            })
            .block();

//        Mono<ClientResponse> responseMono = webClient.post()
//                                                           .uri(CONVERSATION_URL)
//                                                           .contentType(MediaType.APPLICATION_JSON)
//            .retrieve()
//            .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK, clientResponse -> {
//                String cookieValue = clientResponse.cookies().get("hf-chat").stream().findFirst().map(HttpCookie::getValue).orElse(null);
//                Mono<ConversationResponse> mono = clientResponse.bodyToMono(ConversationResponse.class);
//            })


        return conversationResponse;
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaResponse {
        private KoAlpacaResponseToken token;
        private String generated_text;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaResponseToken {
        private String id;
        private String text;
        private double logprob;
        private boolean special;

    }

    @Getter
    @Setter
    @With
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class ConversationResponse {
        private String conversationId;
        private String cookieValue;
    }

    /**
     * {
     *   "inputs": "서울 날씨는 어떤가요?",
     *   "parameters": {
     *     "temperature": 0.9,
     *     "top_p": 0.95,
     *     "truncate": 1000,
     *     "watermark": false,
     *     "no_repeat_ngram_size": 6,
     *     "max_new_tokens": 1024,
     *     "stop": [
     *       "<|endoftext|>",
     *       "###",
     *       "\n###"
     *     ],
     *     "return_full_text": false
     *   },
     *   "stream": true,
     *   "options": {
     *     "use_cache": false
     *   }
     * }
     */
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaRequest {
        private String inputs;
        private KoAlpacaRequestParameter parameters;
        private boolean stream;
        private KoAlpacaRequestOption options;

        public static KoAlpacaRequest defaultKoAlpacaRequest(String input) {
            return KoAlpacaRequest.builder()
                                  .inputs(input)
                                  .parameters(KoAlpacaRequestParameter.defaultKoAlpacaRequestParameter())
                                  .stream(true)
                                  .options(KoAlpacaRequestOption.builder().use_cache(false).build())
                                  .build();
        }

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaRequestParameter {
        private double temperature;
        private double top_p;
        private int truncate;
        private boolean watermark;
        private int no_repeat_ngram_size;
        private int max_new_tokens;
        private String[] stop;
        private boolean return_full_text;

        public static KoAlpacaRequestParameter defaultKoAlpacaRequestParameter() {
            return KoAlpacaRequestParameter.builder()
                                           .temperature(0.9)
                                           .top_p(0.95)
                                           .truncate(1000)
                                           .watermark(false)
                                           .no_repeat_ngram_size(6)
                                           .max_new_tokens(1024)
                                           .stop(new String[]{"<|endoftext|>", "###", "\n###"})
                                           .return_full_text(false)
                                           .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaRequestOption {
        private boolean use_cache;

    }
}
