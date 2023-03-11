package fr.funixgaming.funixbot.discord.commands.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommand {

    String getName();
    String getDescription();
    void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent);
    void runCommand(@NonNull MessageReceivedEvent message);
}
