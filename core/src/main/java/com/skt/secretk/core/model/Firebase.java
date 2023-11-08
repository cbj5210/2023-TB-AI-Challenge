package com.skt.secretk.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Firebase {
    private String user; // 사번 : 1111111
    private String type; // 요청 request, 응답 response
    private String message; // 요청/응답 내용
    private String responseType; // 응답 타입, text, url, file
    private String createTime; // yyyy-MM-dd HH:mm:ss
}
