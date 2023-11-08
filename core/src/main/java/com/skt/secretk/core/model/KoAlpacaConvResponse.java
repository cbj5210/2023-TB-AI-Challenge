package com.skt.secretk.core.model;

import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@Getter
@With
@AllArgsConstructor
@RequiredArgsConstructor
public class KoAlpacaConvResponse {
    private String conversationId;
    private String cookieValue;

    public boolean invalid() {
        return StringUtils.isAnyBlank(conversationId, conversationId);
    }
}
