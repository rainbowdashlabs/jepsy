package de.chojo.jepsy;

import de.chojo.jepsy.data.JepData;
import de.chojo.jepsy.document.JepDocument;
import de.chojo.jepsy.document.JepDocumentRef;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class DocumentRenderService {
    private static final Logger log = getLogger(DocumentRenderService.class);
    private final JepData jepData;
    private static final String URL = "https://openjdk.java.net/jeps/";

    public DocumentRenderService(JepData jepData) {
        this.jepData = jepData;
    }

    public void renderDocument(InteractionHook hook, JepDocumentRef documentRef) {
        jepData.getDocument(documentRef).thenAccept(document -> {
            var embed = new EmbedBuilder()
                    .setTitle(document.name(), URL + document.jep())
                    .setDescription("Please choose a chapter")
                    .build();

            hook.editOriginalEmbeds(embed).setActionRows(getChapterRows(document)).complete();
        }).exceptionally(this::logError);
    }

    public void renderDocument(ButtonClickEvent event, JepDocumentRef documentRef) {
        jepData.getDocument(documentRef).thenAccept(document -> {
            var embed = new EmbedBuilder()
                    .setTitle(document.name(), URL + document.jep())
                    .setDescription("Please choose a chapter")
                    .build();

            event.getMessage().editMessageEmbeds(embed).setActionRows(getChapterRows(document)).complete();
        }).exceptionally(this::logError);
    }

    public void renderDocumentChapter(ButtonClickEvent event, JepDocumentRef ref, String chapter) {
        jepData.getDocument(ref).thenAccept(document -> {
            var embed = getDocumentChapterEmbed(document, chapter);
            event.getMessage().editMessageEmbeds(embed).setActionRows(getChapterRows(document)).queue();
        }).exceptionally(this::logError);
    }

    private MessageEmbed getDocumentChapterEmbed(JepDocument document, String chapter) {
        var content = document.chapters().get(chapter);
        return new EmbedBuilder()
                .setTitle(document.name(), URL + document.jep())
                .addField(chapter, StringUtils.abbreviate(content, "...", 1020), false)
                .build();
    }

    private List<ActionRow> getChapterRows(JepDocument document) {
        var buttons = document.chapters().entrySet().stream()
                .map(e -> Button.primary(document.jep() + ":" + e.getKey(), e.getKey()))
                .collect(Collectors.toList());
        return getComponentRows(buttons);
    }

    public void renderMultiple(InteractionHook hook, List<JepDocumentRef> documentRefs) {
        List<Button> buttons = new ArrayList<>();
        for (var documentRef : documentRefs) {
            buttons.add(Button.primary(String.valueOf(documentRef.jep()), documentRef.name()));
        }

        hook.editOriginal("Found multiple Entries").setActionRows(getComponentRows(buttons)).queue();
    }

    private List<ActionRow> getComponentRows(List<? extends Component> components) {
        var rows = new ArrayList<ActionRow>();
        var from = 0;
        var to = 5;

        var splitting = new ArrayList<>(components);

        while (from < splitting.size()) {
            rows.add(ActionRow.of(splitting.subList(from, Math.min(to, splitting.size()))));
            from += 5;
            to += 5;
        }

        return rows;
    }

    private Void logError(Throwable throwable){
        log.error("", throwable);
        return Void.TYPE.cast(null);
    }
}
