package fr.funixgaming.funixbot.commands;

import fr.funixgaming.funixbot.commands.utils.BotCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommandMe extends BotCommand {

    public CommandMe() {
        super("me", "Récupère les informations de ton compte Discord !");
    }

    @Override
    public void onUserCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {

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
