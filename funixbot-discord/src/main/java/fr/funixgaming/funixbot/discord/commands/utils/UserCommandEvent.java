package fr.funixgaming.funixbot.commands.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface UserCommandEvent {

    void onUserCommand(final @NonNull SlashCommandInteractionEvent interactionEvent);

}
