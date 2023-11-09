package com.skt.secretk.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleNlpRequest {

    private NlpRequestDocument document;
    private String encodingType;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NlpRequestDocument {
        private String type;
        private String content;
    }
}
