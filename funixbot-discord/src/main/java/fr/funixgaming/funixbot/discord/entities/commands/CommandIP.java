package fr.funixgaming.funixbot.discord.entities.commands;

import fr.funixgaming.funixbot.discord.entities.commands.utils.DiscordCommand;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

@Getter
public class CommandIP extends DiscordCommand {

    private final String name = "ip";
    private final String description = "Récupère l'IP et les informations du serveur Minecraft Pacifista !";

    public CommandIP(final JDA jda) {
        super(jda);
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        final EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Pacifista Minecraft");
        embed.setDescription("Serveur minecraft survie");
        embed.addField("Site Web", "https://pacifista.fr", true);
        embed.addField("IP de connection", "play.pacifista.fr", true);
        embed.addField("Version", "1.19.2", true);
        embed.setColor(Color.decode("#2cafff"));

        final Button btn = Button.link(
                "https://pacifista.fr",
                "Accéder au site Web"
        ).withEmoji(
                Emoji.fromEmote(":globe_web:", Long.parseLong("1078054633592852581"), false)
        );

        interactionEvent.replyEmbeds(embed.build()).addActionRow(btn).queue();
    }
}
