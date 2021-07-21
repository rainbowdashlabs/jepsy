package de.chojo.jepsy.document;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentMeta {
    private final String owner;
    private final String type;
    private final String scope;
    private final String status;
    private final String release;
    private final String component;
    private final List<String> related;
    private final List<String> reviewed;
    private final List<String> endorsed;
    private final LocalDateTime created;
    private final LocalDateTime updated;
    private final String issue;

    public DocumentMeta(String owner, String type, String scope, String status, String release, String component, List<String> related, List<String> reviewed, List<String> endorsed, LocalDateTime created, LocalDateTime updated, String issue) {
        this.owner = owner;
        this.type = type;
        this.scope = scope;
        this.status = status;
        this.release = release;
        this.component = component;
        this.related = related;
        this.reviewed = reviewed;
        this.endorsed = endorsed;
        this.created = created;
        this.updated = updated;
        this.issue = issue;
    }
}
