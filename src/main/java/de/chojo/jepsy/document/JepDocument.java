package de.chojo.jepsy.document;

import java.util.Map;

public class JepDocument {
    private final DocumentMeta meta;
    private final Map<String, String> chapters;

    public JepDocument(DocumentMeta meta, Map<String, String> chapters) {
        this.meta = meta;
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "JepDocument{" +
                "meta=" + meta +
                ", chapters=" + chapters +
                '}';
    }
}
