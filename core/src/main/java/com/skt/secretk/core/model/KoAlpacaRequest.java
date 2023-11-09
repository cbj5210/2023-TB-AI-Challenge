package com.skt.secretk.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class KoAlpacaRequest {
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

    @Builder
    @Getter
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

    @Builder
    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaRequestOption {
        private boolean use_cache;

    }
}
