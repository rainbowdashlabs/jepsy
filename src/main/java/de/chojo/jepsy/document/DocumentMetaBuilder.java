package de.chojo.jepsy.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentMetaBuilder {
    private String owner;
    private String type;
    private String scope;
    private String status;
    private String release;
    private String component;
    private final List<String> related = new ArrayList<>();
    private final List<String> reviewed = new ArrayList<>();
    private final List<String> endorsed = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime updated;
    private String issue;

    public DocumentMetaBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public DocumentMetaBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public DocumentMetaBuilder setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public DocumentMetaBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public DocumentMetaBuilder setRelease(String release) {
        this.release = release;
        return this;
    }

    public DocumentMetaBuilder setComponent(String component) {
        this.component = component;
        return this;
    }

    public DocumentMetaBuilder addRelated(String... related) {
        this.related.addAll(List.of(related));
        return this;
    }

    public DocumentMetaBuilder setReviewed(String... reviewed) {
        this.reviewed.addAll(List.of(reviewed));
        return this;
    }

    public DocumentMetaBuilder addEndorsed(String... endorsed) {
        this.endorsed.addAll(List.of(endorsed));
        return this;
    }

    public DocumentMetaBuilder setCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public DocumentMetaBuilder setUpdated(LocalDateTime updated) {
        this.updated = updated;
        return this;
    }

    public DocumentMetaBuilder setIssue(String issue) {
        this.issue = issue;
        return this;
    }

    public DocumentMeta build() {
        return new DocumentMeta(owner, type, scope, status, release, component, related, reviewed, endorsed, created,
                updated, issue);
    }
}
