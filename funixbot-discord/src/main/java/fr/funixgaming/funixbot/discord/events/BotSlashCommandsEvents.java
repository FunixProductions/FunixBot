package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.entities.commands.utils.SlashCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BotSlashCommandsEvents extends ListenerAdapter {

    private final FunixBot funixBot;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (SlashCommand cmd : funixBot.getBotCommands()) {
            if (cmd.getName().equals(event.getName())) {
                cmd.runCommand(event);
            }
        }
    }
}
