package com.skt.secretk.core;

import org.junit.jupiter.api.Test;

public class LuceneTest {
    @Test
    public void test() {

    }

    private static SynonymMap getSynonyms() {
        // de-duplicate rules when loading:
        boolean dedup = Boolean.TRUE;
        // include original word in index:
        boolean includeOrig = Boolean.TRUE;

        SynonymMap.Builder builder = new SynonymMap.Builder(dedup);

        // examples of single synonyms:
        builder.add(new CharsRef("can't"), new CharsRef("cannot"), includeOrig);
        builder.add(new CharsRef("what's"), new CharsRef("what is"), includeOrig);

        // example with multiple synonyms:
        CharsRefBuilder multiWordCharsRef = new CharsRefBuilder();
        SynonymMap.Builder.join(new String[]{"do not", "does not"}, multiWordCharsRef);
        builder.add(new CharsRef("don't"), multiWordCharsRef.get(), includeOrig);

        SynonymMap synonymMap = null;
        try {
            synonymMap = builder.build();
        } catch (IOException ex) {
            System.err.print(ex);
        }
        return synonymMap;
    }
}
