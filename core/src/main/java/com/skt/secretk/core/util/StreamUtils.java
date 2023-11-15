package com.skt.secretk.core.util;

import java.util.Collection;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T> Stream<T> ofNullable(Collection<T> collection) {
        return collection != null ? collection.stream() : Stream.empty();
    }
}