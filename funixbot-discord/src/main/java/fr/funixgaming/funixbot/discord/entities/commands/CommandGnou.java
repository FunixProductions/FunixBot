package fr.funixgaming.funixbot.discord.entities.commands;

import fr.funixgaming.funixbot.discord.entities.commands.utils.DiscordCommand;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Getter
public class CommandGnou extends DiscordCommand {

    private final String name = "gnou";
    private final String description = "Blague drôle 👀";

    public CommandGnou(JDA jda) {
        super(jda);
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        final String reply = "Alors c'est l'histoire d'un gnou qui se balade dans la savane et qui croise un autre groupe de gnou." + System.lineSeparator() +
                "L'autre groupe de gnou le voyant tout seul lui a donc demandé : " + System.lineSeparator() +
                "Eh viens avec gnou :water_buffalo:";

        interactionEvent.reply(reply).setEphemeral(true).queue();
    }
}
