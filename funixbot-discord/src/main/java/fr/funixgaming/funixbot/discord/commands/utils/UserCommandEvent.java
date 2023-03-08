package fr.gamecreep.bot.commands.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface UserCommandEvent {

    /*
        Principalement repris du FunixBot Twitch.
     */

    void onUserCommand(final @NonNull SlashCommandInteractionEvent interactionEvent);

}