package com.skt.secretk.core.enum_;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MassageType {
    TEXT("text"),
    URL("url"),
    FILE("file");

    private final String name;
}
