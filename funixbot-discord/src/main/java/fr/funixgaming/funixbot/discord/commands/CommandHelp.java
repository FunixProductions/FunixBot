package fr.funixgaming.funixbot.discord.commands;

import fr.funixgaming.funixbot.discord.commands.utils.CommandList;
import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;

public class CommandHelp implements SlashCommand {

    private String name = "help";
    private String description = "Récupère les commandes disponibles !";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {

        CommandList cmdList;
        cmdList = new CommandList();
        List<SlashCommand> commandList = cmdList.getList();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(interactionEvent.getJDA().getSelfUser().getAvatarUrl());
        embed.setAuthor(interactionEvent.getJDA().getSelfUser().getAsTag(), null, interactionEvent.getJDA().getSelfUser().getAvatarUrl());
        embed.setFooter(interactionEvent.getUser().getAsTag(), interactionEvent.getUser().getAvatarUrl());
        embed.setColor(new Color(44,175,255));
        embed.setDescription("Voici toute les commandes du bot !");

        for (SlashCommand cmd : commandList) {
            embed.addField(cmd.getName(), cmd.getDescription(), false);
        }

        interactionEvent.replyEmbeds(embed.build()).queue();

    }
}
