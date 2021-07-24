package de.chojo.jepsy.document;

import java.util.LinkedHashMap;
import java.util.Map;

public class JepDocumentBuilder {
    private final int number;
    private final String name;
    private JepDocumentMeta meta;
    private final Map<String, String> chapters = new LinkedHashMap<>();

    public JepDocumentBuilder(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public JepDocumentBuilder setMeta(JepDocumentMeta meta) {
        this.meta = meta;
        return this;
    }

    public JepDocumentBuilder addChapter(String name, String content) {
        this.chapters.put(name, content);
        return this;
    }

    public JepDocument build() {
        return new JepDocument(number, name, meta, chapters);
    }

    public int number() {
        return number;
    }

    public String name() {
        return name;
    }

    public JepDocumentMeta meta() {
        return meta;
    }

    public Map<String, String> chapters() {
        return chapters;
    }
}
