package com.skt.secretk.core.util;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T> Stream<T> ofNullable(Collection<T> collection) {
        return collection != null ? collection.stream() : Stream.empty();
    }

    public static <T> Stream<T> ofNullableAndNonNull(Collection<T> collection) {
        return collection != null ? collection.stream().filter(Objects::nonNull) : Stream.empty();
    }

    public static <T> Stream<T> optionalToStream(Optional<T> optional) {
        return optional.map(Stream::of).orElse(Stream.empty());
    }
}