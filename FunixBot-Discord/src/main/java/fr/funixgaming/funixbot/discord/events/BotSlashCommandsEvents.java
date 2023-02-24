package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.commands.*;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class BotSlashCommandsEvents extends ListenerAdapter {

    private final FunixBot funixBot;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        switch (event.getName()) {
            case "gnou" :
                Commands.gnou.run(event);
                break;
            case "ip":
                EmbedCommands.ip.run(event);
                break;
            case "ping":
                EmbedCommands.ping.run(event);
                break;
            case "me":
                EmbedCommands.me.run(event);
                break;
            case "help":
                EmbedCommands.help.run(event);
        }

    }
}
