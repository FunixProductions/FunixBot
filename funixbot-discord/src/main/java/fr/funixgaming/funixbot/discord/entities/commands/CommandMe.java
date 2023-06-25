package fr.funixgaming.funixbot.discord.entities.commands;

import fr.funixgaming.funixbot.discord.entities.commands.utils.DiscordCommand;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class CommandMe extends DiscordCommand {

    private final String name = "me";
    private final String description = "Récupère les informations de ton compte Discord !";

    public CommandMe(final JDA jda) {
        super(jda);
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE);
        final EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(interactionEvent.getUser().getName(), null, interactionEvent.getUser().getAvatarUrl());
        embed.setDescription("Voici donc les infos principales de ton compte");
        embed.addField("Pseudo", interactionEvent.getUser().getName(), false);
        embed.addField("Date de création", interactionEvent.getUser().getTimeCreated().format(formatter), false);
        embed.setColor(Color.decode("#2cafff"));
        embed.setThumbnail(interactionEvent.getUser().getAvatarUrl());

        interactionEvent.replyEmbeds(embed.build()).queue();
    }
}
