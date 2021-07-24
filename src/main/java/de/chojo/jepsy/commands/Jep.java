package de.chojo.jepsy.commands;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import de.chojo.jepsy.DocumentRenderService;
import de.chojo.jepsy.data.JepData;
import de.chojo.jepsy.document.JepDocumentRef;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class Jep extends SimpleCommand {
    private final JepData jepData;
    private final DocumentRenderService renderService;

    public Jep(JepData jepData, DocumentRenderService renderService) {
        super("jep", null, "jep",
                subCommandBuilder()
                        .add("search", "Access data about jep.")
                        .add("show", "show a jep",
                                argsBuilder()
                                        .add(OptionType.INTEGER, "jep_number", "number of the jep")
                                        .add(OptionType.STRING, "name", "name of the jep")
                                        .build())
                        .build(), Permission.UNKNOWN);
        this.jepData = jepData;
        this.renderService = renderService;
    }

    @Override
    public boolean onCommand(MessageEventWrapper eventWrapper, CommandContext context) {
        return false;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, SlashCommandContext context) {
        var cmd = event.getSubcommandName();
        if ("show".equalsIgnoreCase(cmd)) {
            show(event, context);
        }
    }

    private void show(SlashCommandEvent event, SlashCommandContext context) {
        var jepNumber = event.getOption("jep_number");
        if (jepNumber != null) {
            jepData.byId((int) jepNumber.getAsLong()).thenAccept(refs -> {
                event.deferReply().queue();
                handleRefs(event.getHook(), refs);
            });
        }
    }

    private void handleRefs(InteractionHook hook, List<JepDocumentRef> refs) {
        if (refs.isEmpty()) {
            hook.editOriginal("JEP not found.").queue();
            return;
        }

        if (refs.size() == 1) {
            var documentRef = refs.get(0);
            renderService.renderDocument(hook, documentRef);
            return;
        }

        renderService.renderMultiple(hook, refs);
    }
}
