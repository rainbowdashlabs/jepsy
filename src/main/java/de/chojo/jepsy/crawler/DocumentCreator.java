package de.chojo.jepsy.crawler;

import de.chojo.jepsy.document.JepDocument;
import de.chojo.jepsy.document.JepDocumentBuilder;
import de.chojo.jepsy.document.JepDocumentMeta;
import de.chojo.jepsy.document.JepDocumentMetaBuilder;
import de.chojo.jepsy.parser.MarkdownParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

public class DocumentCreator {
    private static final Logger log = getLogger(DocumentCreator.class);
    private static final String URL = "https://openjdk.java.net/jeps/";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public CompletableFuture<Optional<JepDocument>> retrieveDocument(int number) {
        return CompletableFuture.supplyAsync(() -> getDocument(number));
    }

    private Optional<JepDocument> getDocument(int jep) {

        var optDocument = getSoupDocument(jep);
        if (optDocument.isEmpty()) {
            log.warn("No document found.");
            return Optional.empty();
        }

        var builder = new JepDocumentBuilder(jep, optDocument.get().title());

        var optMeta = getDocumentMeta(optDocument.get());

        if (optMeta.isEmpty()) {
            log.warn("No document meta");
            return Optional.empty();
        }

        builder.setMeta(optMeta.get());

        return getDocumentContent(builder, optDocument.get());
    }

    private Optional<JepDocument> getDocumentContent(JepDocumentBuilder builder, Document document) {
        var mainElement = getMainElement(document);

        var markdowns = mainElement.getElementsByClass("markdown");

        if (markdowns.isEmpty()) {
            log.warn("No markdown section");
            return Optional.empty();
        }

        var markdown = markdowns.get(0);

        String chapter = null;
        List<String> entries = new LinkedList<>();

        for (var ele : markdown.children()) {
            if (ele.tag().getName().startsWith("h")) {
                if (chapter != null) {
                    builder.addChapter(chapter, MarkdownParser.htmlToMarkdow(String.join("\n", entries)));
                    entries.clear();
                }
                chapter = ele.text();
                continue;
            }
            entries.add(preCleanElement(ele));
        }
        builder.addChapter(chapter, MarkdownParser.htmlToMarkdow(String.join("\n", entries)));

        return Optional.of(builder.build());
    }

    private String preCleanElement(Element element) {
        var code = element.getElementsByTag("pre");
        if (!code.isEmpty()) {
            return code.html();
        }

        var anchors = element.getElementsByTag("a");

        var html = element.html();
        for (var anchor : anchors) {
            var href = anchor.attr("href");
            var absHref = anchor.attr("abs:href");
            html = html.replace("href=\"" + href + "\"", "href=\"" + absHref + "\"");
        }

        return element.html();
    }

    private Optional<JepDocumentMeta> getDocumentMeta(Document document) {
        var builder = new JepDocumentMetaBuilder();

        var mainElement = getMainElement(document);
        var heads = mainElement.getElementsByClass("head");
        if (heads.isEmpty()) {
            log.warn("No head section");
            return Optional.empty();
        }

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

            addMeta(type, builder, value);
        }

        return Optional.of(builder.build());
    }

    private void addMeta(String type, JepDocumentMetaBuilder builder, Element element) {
        var anchor = element.getElementsByTag("a");
        if (!anchor.isEmpty()) {
            var absUrl = anchor.get(0).absUrl("href");
            builder.addMeta(type, MarkdownParser.url(element.text().trim(), absUrl));
            return;
        }
        if (element.text().contains(",")) {
            for (var e : element.text().split(",")) {
                builder.addMeta(type, e);
            }
            return;
        }
        builder.addMeta(type, element.text());
    }

    private Element getMainElement(Document document) {
        return document.getElementById("main");
    }

    private Optional<Document> getSoupDocument(int jep) {
        try {
            return Optional.of(Jsoup.newSession().url(getJepUrl(jep)).get());
        } catch (IOException e) {
            log.warn("Could not retrieve document.", e);
            return Optional.empty();
        }
    }

    private String getJepUrl(int jep) {
        return URL + jep;
    }
}
