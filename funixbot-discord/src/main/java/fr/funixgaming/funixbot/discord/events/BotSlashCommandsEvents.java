package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.commands.utils.SlashCommand;
import fr.funixgaming.funixbot.commands.utils.CommandList;
import fr.funixgaming.funixbot.discord.FunixBot;
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

        CommandList cmdList;
        cmdList = new CommandList();
        List<SlashCommand> commandList = cmdList.getList();

        for (SlashCommand cmd : commandList) {
            if (cmd.getName().equals(event.getName())) {
                cmd.onUserCommand(event);
            }
        }
    }
}
