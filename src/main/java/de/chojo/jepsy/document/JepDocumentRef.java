package de.chojo.jepsy.document;

public class JepDocumentRef {
    private final int jep;
    private final String name;

    public JepDocumentRef(int jep, String name) {
        this.jep = jep;
        this.name = name;
    }

    public int jep() {
        return jep;
    }

    public String name() {
        return name;
    }
}
