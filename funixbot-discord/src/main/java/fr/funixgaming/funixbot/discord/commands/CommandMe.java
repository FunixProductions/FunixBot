package fr.funixgaming.funixbot.discord.commands;

import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommandMe implements SlashCommand {

    private String name = "me";
    private String description = "Récupère les informations de ton compte Discord !";

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(interactionEvent.getUser().getAsTag(), null, interactionEvent.getUser().getAvatarUrl());
        embed.setDescription("Voici donc les infos principales de ton compte");
        embed.addField("Pseudo", interactionEvent.getUser().getAsTag(), false);
        embed.addField("Date de création", interactionEvent.getUser().getTimeCreated().format(formatter), false);
        embed.setColor(Color.decode("#2cafff"));
        embed.setThumbnail(interactionEvent.getUser().getAvatarUrl());

        interactionEvent.replyEmbeds(embed.build()).queue();

    }
}
