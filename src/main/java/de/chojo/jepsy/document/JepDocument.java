package de.chojo.jepsy.document;

import java.util.Map;

public class JepDocument extends JepDocumentRef {
    private final JepDocumentMeta meta;
    private final Map<String, String> chapters;

    public JepDocument(int jep, String name, JepDocumentMeta meta, Map<String, String> chapters) {
        super(jep, name);
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

    public JepDocumentMeta meta() {
        return meta;
    }

    public Map<String, String> chapters() {
        return chapters;
    }
}
