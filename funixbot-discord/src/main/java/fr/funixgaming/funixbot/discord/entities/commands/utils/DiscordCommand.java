package fr.funixgaming.funixbot.discord.entities.commands.utils;

import kotlin.Pair;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collection;

/**
 * Class used to create commands
 * We send JDA to create the command in the API when contructor
 */
@Getter
public abstract class DiscordCommand implements SlashCommand {

    protected DiscordCommand(final JDA jda) {
        jda.upsertCommand(this.getName(), this.getDescription()).queue();
    }

    protected DiscordCommand(final JDA jda, final Collection<Pair<String, String>> args) {
        final SlashCommandData discordCommand = Commands.slash(this.getName(), this.getDescription());

        for (Pair<String, String> arg : args) {
            discordCommand.addOption(OptionType.STRING, arg.getFirst(), arg.getSecond());
        }

        jda.upsertCommand(discordCommand).queue();
    }

    protected DiscordCommand(final JDA jda, Pair<String, String> arg) {
        final SlashCommandData discordCommand = Commands.slash(this.getName(), this.getDescription());

        discordCommand.addOption(OptionType.STRING, arg.getFirst(), arg.getSecond());
        jda.upsertCommand(discordCommand).queue();
    }

}
