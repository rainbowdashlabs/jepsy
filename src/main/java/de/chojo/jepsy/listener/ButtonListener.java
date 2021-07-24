package de.chojo.jepsy.listener;

import de.chojo.jepsy.DocumentRenderService;
import de.chojo.jepsy.data.JepData;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class ButtonListener extends ListenerAdapter {
    private static final Logger log = getLogger(ButtonListener.class);
    private static final Pattern JEP = Pattern.compile("[0-9]+?");
    private static final Pattern JEP_CHAPTER = Pattern.compile("(?<jep>[0-9]+?):(?<chapter>.+?)");
    private final JepData jepData;
    private final DocumentRenderService renderService;

    public ButtonListener(JepData jepData, DocumentRenderService renderService) {
        this.jepData = jepData;
        this.renderService = renderService;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        event.deferEdit().queue();
        var id = event.getButton().getId();
        var matcher = JEP.matcher(id);
        if (matcher.matches()) {
            jepData.byId(Integer.parseInt(id)).thenAccept(ref -> {
                renderService.renderDocument(event, ref.get(0));
            });
            return;
        }
        matcher = JEP_CHAPTER.matcher(id);
        if (matcher.matches()) {
            var chapter = matcher.group("chapter");
            jepData.byId(Integer.parseInt(matcher.group("jep"))).thenAccept(ref -> {
                renderService.renderDocumentChapter(event, ref.get(0), chapter);
            }).exceptionally(thr -> {
                log.error("", thr);
                return null;
            } );
        }
    }
}
