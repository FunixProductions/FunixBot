package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.BotColors;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.configs.BotConfigGenerated;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleMessageHandler {

    private final BotConfigGenerated botConfigGenerated;
    private final BotConfig botConfig;
    private final JDA jda;
    private final FunixBot funixBot;

    public RoleMessageHandler(BotConfigGenerated botConfigGenerated,
                              BotConfig botConfig,
                              JDA jda,
                              FunixBot funixBot) {
        this.botConfigGenerated = botConfigGenerated;
        this.botConfig = botConfig;
        this.jda = jda;
        this.funixBot = funixBot;

        try {
            setMessageRoleChoice();
        } catch (FunixBotException e) {
            throw new RuntimeException("Erreur lors du chargement du role handler.", e);
        }
    }

    private void setMessageRoleChoice() throws FunixBotException {
        final TextChannel rolesChannel = jda.getTextChannelById(botConfig.getRolesChannelId());

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

    public void manageRolesByReactions(final User user, final MessageReaction messageReaction, boolean addReaction) {
        final String messageId = messageReaction.getMessageId();

        if (this.botConfigGenerated.getMessageRolesChoiceId().equals(messageId)) {
            final BotEmotes botEmotes = funixBot.getBotEmotes();
            final BotRoles botRoles = funixBot.getBotRoles();
            final Guild guild = funixBot.getBotGuild();
            final Emote emote = messageReaction.getReactionEmote().getEmote();

            if (addReaction) {
                if (emote.equals(botEmotes.getTwitchEmote())) {
                    guild.addRoleToMember(user, botRoles.getTwitchNotifRole()).queue();
                } else if (emote.equals(botEmotes.getYoutubeEmote())) {
                    guild.addRoleToMember(user, botRoles.getYoutubeNotifRole()).queue();
                } else if (emote.equals(botEmotes.getTiktokEmote())) {
                    guild.addRoleToMember(user, botRoles.getTiktokNotifRole()).queue();
                }
            } else {
                if (emote.equals(botEmotes.getTwitchEmote())) {
                    guild.removeRoleFromMember(user, botRoles.getTwitchNotifRole()).queue();
                } else if (emote.equals(botEmotes.getYoutubeEmote())) {
                    guild.removeRoleFromMember(user, botRoles.getYoutubeNotifRole()).queue();
                } else if (emote.equals(botEmotes.getTiktokEmote())) {
                    guild.removeRoleFromMember(user, botRoles.getTiktokNotifRole()).queue();
                }
            }
        }
    }

    private void createMessageRoleChoice(final TextChannel rolesChannel) {
        final BotEmotes botEmotes = funixBot.getBotEmotes();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(null, null, jda.getSelfUser().getAvatarUrl());
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
