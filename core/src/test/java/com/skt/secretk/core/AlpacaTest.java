package com.skt.secretk.core;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class AlpacaTest {
    private static final String CONVERSATION_URL = "https://chat.koalpaca.com/conversation";
    private static final int TIMEOUT = 10000;
    private WebClient webClient;
    @Test
    public void test() {
        webClient = createWebClient();

        String inputMessage = "점심 메뉴 추천해줘";
        KoAlpacaConversationResponse conversationResult = getConversation();
        System.out.println("conversationId : " + conversationResult.getConversationId());

        execute(conversationResult, inputMessage);
    }

    private void execute(KoAlpacaConversationResponse conversationResult, String question) {

        MultiValueMap<String, String> cookieMap = new LinkedMultiValueMap<String, String>();
        cookieMap.add("hf-chat", conversationResult.getCookie());

        KoAlpacaResponse response = webClient.post()
                                             .uri(CONVERSATION_URL + "/" + conversationResult.getConversationId())
                                             .cookie("hf-chat", conversationResult.getCookie())
                                             .header("Origin", "https://chat.koalpaca.com")
                                             .header("Referer", CONVERSATION_URL + "/" + conversationResult.getConversationId())
                                             .accept(MediaType.TEXT_EVENT_STREAM)
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

    private KoAlpacaConversationResponse getConversation() {
        return webClient.post()
                        .uri(CONVERSATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .exchangeToMono(response -> {
                            String cookie = Optional.ofNullable(
                                response.cookies().getFirst("hf-chat"))
                                                    .map(HttpCookie::getValue)
                                                    .orElse("");

                             return response.bodyToMono(ConversationResponse.class)
                                            .map(conversationResponse -> {
                                                return new KoAlpacaConversationResponse(cookie, conversationResponse.getConversationId());
                                            })
                                            .onErrorResume(t -> Mono.empty());
                         })
                         .block();
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaConversationResponse {
        private String cookie;
        private String conversationId;

    }

    @Getter
    @Setter
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
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class ConversationResponse {
        private String conversationId;
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
