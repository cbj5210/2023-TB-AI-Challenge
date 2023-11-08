package com.skt.secretk.core.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    private static final int DEFAULT_TIMEOUT = 10000; //10s

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                                          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT)
                                          .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
                                          .followRedirect(true)
                                          .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
                                                                     .addHandlerLast(new WriteTimeoutHandler(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .defaultHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36")
                        .build();
    }
}
