package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.BotColors;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.configs.BotConfigGenerated;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMessageHandler {

    private final BotConfigGenerated botConfigGenerated;
    private final BotConfig botConfig;

    public void setMessageRoleChoice(@NonNull final FunixBot funixBot) throws FunixBotException {
        final TextChannel rolesChannel = funixBot.getJda().getTextChannelById(botConfig.getRolesChannelId());

        if (rolesChannel == null) {
            throw new FunixBotException("Le channel pour le choix des rôles n'existe pas.");
        }

        if (botConfigGenerated.getMessageRolesChoiceId() == null) {
            createMessageRoleChoice(rolesChannel, funixBot);
        } else {
            try {
                rolesChannel.retrieveMessageById(botConfigGenerated.getMessageRolesChoiceId()).complete();
            } catch (ErrorResponseException responseException) {
                if (responseException.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                    createMessageRoleChoice(rolesChannel, funixBot);
                } else {
                    throw new FunixBotException(String.format("Impossible d'envoyer le message de choix de roles. Erreur: %s", responseException.getMessage()));
                }
            }
        }
    }

    private void createMessageRoleChoice(final TextChannel rolesChannel, final FunixBot funixBot) {
        final BotEmotes botEmotes = funixBot.getBotEmotes();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(null, null, funixBot.getJda().getSelfUser().getAvatarUrl());
        embedBuilder.setTitle("Choix des rôles");
        embedBuilder.setDescription(
                "Afin d'éviter de faire des tag everyone et here sur le discord, vous pouvez choisir vos rôles pour recevoir les notifications qui vous intéressent.\n" +
                        "Vous pouvez d'ailleurs ajouter et retirer votre rôle à tout moment."
        );

        embedBuilder.setColor(BotColors.FUNIX_COLOR);
        embedBuilder.addField(botEmotes.getTwitchEmote().getAsMention(), "Les notifications Twitch", true);
        embedBuilder.addField(botEmotes.getYoutubeEmote().getAsMention(), "Les notifications YouTube", true);
        embedBuilder.addField(botEmotes.getTiktokEmote().getAsMention(), "Les notifications TikTok", true);

        rolesChannel.sendMessageEmbeds(embedBuilder.build()).queue((message -> {
            this.botConfigGenerated.setMessageRolesChoiceId(message.getId());

            message.addReaction(botEmotes.getTwitchEmote()).queue();
            message.addReaction(botEmotes.getYoutubeEmote()).queue();
            message.addReaction(botEmotes.getTiktokEmote()).queue();
        }));
    }

}
