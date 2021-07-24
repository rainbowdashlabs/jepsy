package de.chojo.jepsy.document;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JepDocumentMeta {
    private final Map<String, List<String>> meta;

    public JepDocumentMeta(Map<String, List<String>> meta) {
        this.meta = meta;
    }

    public Map<String, List<String>> values() {
        return Collections.unmodifiableMap(meta);
    }

    public static class Relation {
        private final String name;
        private final String link;

        public Relation(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String name() {
            return name;
        }

        public String link() {
            return link;
        }
    }
}
