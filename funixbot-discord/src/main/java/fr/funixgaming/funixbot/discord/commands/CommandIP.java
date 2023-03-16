package fr.funixgaming.funixbot.discord.commands;

import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;
import lombok.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

@Getter
public class CommandIP implements SlashCommand {

    private String name = "ip";
    private String description = "Récupère l'IP et les informations du serveur Minecraft Pacifista !";

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Pacifista Minecraft");
        embed.setDescription("Serveur minecraft survie");
        embed.addField("Site Web", "https://pacifista.fr", true);
        embed.addField("IP de connection", "play.pacifista.fr", true);
        embed.addField("Version", "1.19.2", true);
        embed.setColor(Color.decode("#2cafff"));

        Button btn = Button.link(
                "https://pacifista.fr",
                "Accéder au site Web"
        ).withEmoji(
                Emoji.fromEmote(":globe_web:", Long.parseLong("1078054633592852581"), false)
        );

        interactionEvent.replyEmbeds(embed.build()).addActionRow(btn).queue();
    }
}
