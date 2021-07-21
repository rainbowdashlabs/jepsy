package de.chojo.jepsy.crawler;

import de.chojo.jepsy.document.DocumentMeta;
import de.chojo.jepsy.document.DocumentMetaBuilder;
import de.chojo.jepsy.document.JepDocument;
import de.chojo.jepsy.document.JepDocumentBuilder;
import io.github.furstenheim.CopyDown;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

public class DocumentCreator {
    private static final Logger log = getLogger(DocumentCreator.class);
    private static final String URL = "https://openjdk.java.net/jeps/";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final CopyDown copyDown = new CopyDown();

    public CompletableFuture<Optional<JepDocument>> retrieveDocument(int number) {
        return CompletableFuture.supplyAsync(() -> getDocument(number));
    }

    private Optional<JepDocument> getDocument(int jep) {

        var optDocument = getSoupDocument(jep);
        if (optDocument.isEmpty()) return Optional.empty();

        var optMeta = getDocumentMeta(optDocument.get());

        if (optMeta.isEmpty()) return Optional.empty();

        return getDocumentContent(optDocument.get(), optMeta.get());
    }

    private Optional<JepDocument> getDocumentContent(Document document, DocumentMeta meta) {
        var builder = new JepDocumentBuilder(meta);

        var mainElement = getMainElement(document);

        var markdowns = mainElement.getElementsByClass("markdown");

        if (markdowns.isEmpty()) return Optional.empty();

        var markdown = markdowns.get(0);

        String chapter = null;
        List<String> entries = new LinkedList<>();

        for (var ele : markdown.children()) {
            if (ele.tag().getName().startsWith("h")) {
                if (chapter != null) {
                    builder.addChapter(chapter, String.join("\n", entries));
                    entries.clear();
                }
                chapter = ele.text();
                continue;
            }
            entries.add(preCleanElement(ele));
        }
        builder.addChapter(chapter, String.join("\n", entries));

        return Optional.ofNullable(builder.build());
    }

    private String preCleanElement(Element element) {
        var code = element.getElementsByTag("pre");
        if (!code.isEmpty()) {
            return code.html()                    ;
        }
        return element.html();
    }

    private Optional<DocumentMeta> getDocumentMeta(Document document) {
        var builder = new DocumentMetaBuilder();

        var mainElement = getMainElement(document);
        var heads = mainElement.getElementsByClass("head");
        if (heads.isEmpty()) return Optional.empty();

        var head = heads.get(0);

        var rows = head.getElementsByTag("tr");

        String type = null;
        for (var row : rows) {
            var fields = row.getElementsByTag("td");
            if (fields.size() != 2) {
                continue;
            }

            var typeText = fields.get(0).text();
            if (typeText.isBlank() && type == null) continue;
            if (!typeText.isBlank()) {
                type = typeText;
            }

            var value = fields.get(1);

            log.info("type {} | value {}", type, value.text());

            switch (type.toLowerCase(Locale.ROOT)) {
                case "owner" -> builder.setOwner(value.text());
                case "type" -> builder.setType(value.text());
                case "scope" -> builder.setScope(value.text());
                case "status" -> builder.setStatus(value.text());
                case "release" -> builder.setRelease(value.text());
                case "component" -> builder.setComponent(value.text());
                case "relates to" -> builder.addRelated(extractLink(value));
                case "reviewed by" -> builder.setReviewed(extractNames(value));
                case "endorsed by" -> builder.addEndorsed(extractNames(value));
                case "created" -> builder.setCreated(extractDate(value));
                case "updated" -> builder.setUpdated(extractDate(value));
                case "issue" -> builder.setIssue(extractLink(value));
                default -> log.info("Ignoring type {}", type);
            }
        }

        return Optional.of(builder.build());
    }

    private String[] extractNames(Element element) {
        return element.text().split("\\s?,\\s?");
    }

    private String extractLink(Element element) {
        return element.getElementsByTag("a").attr("abs:href");
    }

    private LocalDateTime extractDate(Element element) {
        return LocalDateTime.parse(element.text(), formatter);
    }

    private Element getMainElement(Document document) {
        return document.getElementById("main");
    }

    private Optional<Document> getSoupDocument(int jep) {
        try {
            return Optional.of(Jsoup.connect(getJepUrl(jep)).get());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String getJepUrl(int jep) {
        return URL + jep;
    }
}
