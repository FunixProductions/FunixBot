package fr.gamecreep.bot.commands;

import fr.gamecreep.bot.commands.utils.BotCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class CommandGnou extends BotCommand {

    public CommandGnou() {
        super("gnou", "Blague drôle 👀");
    }

    @Override
    public void onUserCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {

        String reply = "Alors c'est l'histoire d'un gnou qui se balade dans la savane et qui croise un autre groupe de gnou." + "\nL'autre groupe de gnou le voyant tout seul lui a donc demandé : " + "\nEh viens avec gnou :water_buffalo:";

        interactionEvent.reply(reply).setEphemeral(true).queue();

    }
}
