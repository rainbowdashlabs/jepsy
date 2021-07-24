package de.chojo.jepsy.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JepDocumentMetaBuilder {
    private final Map<String, List<String>> meta = new LinkedHashMap<>();

    public JepDocumentMetaBuilder addMeta(String key, String value) {
        if ("Author".equalsIgnoreCase(key)) {
            key = "Authors";
        }
        meta.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        return this;
    }

    public JepDocumentMeta build() {
        return new JepDocumentMeta(meta);
    }
}
