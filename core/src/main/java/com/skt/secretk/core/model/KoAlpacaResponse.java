package com.skt.secretk.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class KoAlpacaResponse {
    private KoAlpacaResponseToken token;
    private String generated_text;

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class KoAlpacaResponseToken {
        private String id;
        private String text;
        private double logprob;
        private boolean special;
    }
}
