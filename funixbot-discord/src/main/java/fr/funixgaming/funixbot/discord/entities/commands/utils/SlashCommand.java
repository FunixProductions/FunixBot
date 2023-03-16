package fr.funixgaming.funixbot.discord.entities.commands.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommand {
    String getName();
    String getDescription();
    void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent);
}
