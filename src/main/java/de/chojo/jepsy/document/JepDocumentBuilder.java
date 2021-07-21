package de.chojo.jepsy.document;

import java.util.LinkedHashMap;
import java.util.Map;

public class JepDocumentBuilder {
    private final DocumentMeta meta;
    private final Map<String, String> chapters = new LinkedHashMap<>();

    public JepDocumentBuilder(DocumentMeta meta) {
        this.meta = meta;
    }

    public JepDocumentBuilder addChapter(String name, String content) {
        this.chapters.put(name, content);
        return this;
    }

    public JepDocument build() {
        return new JepDocument(meta, chapters);
    }
}
