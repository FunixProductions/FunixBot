package fr.funixgaming.funixbot.commands;

import fr.funixgaming.funixbot.commands.utils.BotCommand;
import fr.funixgaming.funixbot.commands.utils.CommandList;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CommandHelp extends BotCommand {

    public CommandHelp() {
        super("help", "Récupère les commandes disponibles !");
    }

    @Override
    public void onUserCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {

        CommandList cmdList;
        cmdList = new CommandList();
        List<BotCommand> commandList = cmdList.getList();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(interactionEvent.getJDA().getSelfUser().getAvatarUrl());
        embed.setAuthor(interactionEvent.getJDA().getSelfUser().getAsTag(), null, interactionEvent.getJDA().getSelfUser().getAvatarUrl());
        embed.setFooter(interactionEvent.getUser().getAsTag(), interactionEvent.getUser().getAvatarUrl());
        embed.setColor(new Color(44,175,255));
        embed.setDescription("Voici toute les commandes du bot !");

        for (BotCommand cmd : commandList) {
            embed.addField(cmd.getName(), cmd.getDescription(), false);
        }

        interactionEvent.replyEmbeds(embed.build()).queue();

    }
}
