package com.skt.secretk.core.service;

import com.google.api.client.util.Lists;
import com.skt.secretk.core.model.KoAlpacaConvResponse;
import com.skt.secretk.core.model.KoAlpacaRequest;
import com.skt.secretk.core.model.KoAlpacaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlpacaService {
    private static final String KO_ALPACA_URL = "https://chat.koalpaca.com/conversation";
    private static final String COOKIE_KEY = "hf-chat";
    private final WebClient webClient;

    public String query (String message) {
        KoAlpacaConvResponse koAlpacaConvResponse = callPrepareApi();
        return callMainApi(koAlpacaConvResponse, message);
    }

    private KoAlpacaConvResponse callPrepareApi() {
//        return new KoAlpacaConvResponse("654b2e4f9276fd4020bb33a9", "376cc7e5-41cc-4e5b-ac16-e3fcf5c5cf8b"); //test 용
        return webClient.post()
                        .uri(KO_ALPACA_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .exchangeToMono(response -> response.bodyToMono(KoAlpacaConvResponse.class)
                                                            .map(it -> it.withCookieValue(cookieValue(response.cookies()))))
                        .block();
    }

    private String callMainApi(KoAlpacaConvResponse koAlpacaConvResponse, String question) {
        if (koAlpacaConvResponse.invalid()) {
            return StringUtils.EMPTY;
        }

        return webClient.post()
                        .uri(KO_ALPACA_URL + "/" + koAlpacaConvResponse.getConversationId())
                        .cookie(COOKIE_KEY, koAlpacaConvResponse.getCookieValue())
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(KoAlpacaRequest.defaultKoAlpacaRequest(question)), KoAlpacaRequest.class)
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<KoAlpacaResponse>() {})
                        .onErrorResume(e -> {
                            log.error("koAlpaca error.", e);
                            return Flux.just(new KoAlpacaResponse());
                        })
                        .blockLast()
                        .getGenerated_text().trim();
    }


    private String cookieValue(MultiValueMap<String, ResponseCookie> cookieMultiValueMap) {
        return MapUtils.getObject(cookieMultiValueMap, AlpacaService.COOKIE_KEY, Lists.newArrayList()).stream().findFirst().map(HttpCookie::getValue).orElse(null);    }
}
