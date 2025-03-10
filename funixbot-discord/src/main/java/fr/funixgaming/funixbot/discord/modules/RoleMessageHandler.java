package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.BotColors;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfigGenerated;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.TwitchNotificationRole;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.YoutubeNotificationRole;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

@Slf4j
public class RoleMessageHandler {

    private final BotConfigGenerated botConfigGenerated;
    private final FunixBot funixBot;
    private final JDA jda;

    public RoleMessageHandler(BotConfigGenerated botConfigGenerated,
                              FunixBot funixBot,
                              JDA jda) {
        this.botConfigGenerated = botConfigGenerated;
        this.funixBot = funixBot;
        this.jda = jda;

        try {
            setMessageRoleChoice();
        } catch (FunixBotException e) {
            throw new RuntimeException("Erreur lors du chargement du role handler.", e);
        }
    }

    private void setMessageRoleChoice() throws FunixBotException {
        final TextChannel rolesChannel = jda.getTextChannelById(funixBot.getBotConfig().getRolesChannelId());

        if (rolesChannel == null) {
            throw new FunixBotException("Le channel pour le choix des rôles n'existe pas.");
        }

        if (botConfigGenerated.getMessageRolesChoiceId() == null) {
            createMessageRoleChoice(rolesChannel);
        } else {
            try {
                rolesChannel.retrieveMessageById(botConfigGenerated.getMessageRolesChoiceId()).complete();
            } catch (ErrorResponseException responseException) {
                if (responseException.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                    createMessageRoleChoice(rolesChannel);
                } else {
                    throw new FunixBotException(String.format("Impossible d'envoyer le message de choix de roles. Erreur: %s", responseException.getMessage()));
                }
            }
        }
    }

    private void createMessageRoleChoice(final TextChannel rolesChannel) {
        final BotEmotes botEmotes = funixBot.getBotEmotes();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(BotColors.FUNIX_COLOR)
                .setTitle("Choix des rôles")
                .setDescription(
                        "Afin d'éviter de faire des tag everyone et here sur le discord, vous pouvez choisir vos rôles pour recevoir les notifications qui vous intéressent.\n" +
                                "Vous pouvez d'ailleurs ajouter et retirer votre rôle à tout moment."
                )
                .addField(botEmotes.getTwitchEmote().getFormatted(), "Les notifications Twitch", true)
                .addField(botEmotes.getYoutubeEmote().getFormatted(), "Les notifications YouTube", true)
                .addField(botEmotes.getTiktokEmote().getFormatted(), "Les notifications TikTok", true);

        Button twitchbtn = Button.primary(TwitchNotificationRole.NAME, botEmotes.getTwitchEmote());
        Button youtubebtn = Button.primary(YoutubeNotificationRole.NAME, botEmotes.getYoutubeEmote());

        rolesChannel.sendMessageEmbeds(embed.build()).setActionRow(twitchbtn, youtubebtn).queue((message -> {
            this.botConfigGenerated.setMessageRolesChoiceId(message.getId());
        }));
    }

}
