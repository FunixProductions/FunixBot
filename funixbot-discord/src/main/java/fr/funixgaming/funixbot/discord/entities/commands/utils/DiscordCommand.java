package fr.funixgaming.funixbot.discord.entities.commands.utils;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;

/**
 * Class used to create commands
 * We send JDA to create the command in the API when contructor
 */
@Getter
public abstract class DiscordCommand implements SlashCommand {

    protected DiscordCommand(final JDA jda) {
        jda.upsertCommand(this.getName(), this.getDescription()).queue();
    }

}
