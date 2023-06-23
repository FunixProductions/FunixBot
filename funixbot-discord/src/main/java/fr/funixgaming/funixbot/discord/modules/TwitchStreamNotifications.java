package fr.funixgaming.funixbot.discord.modules;

import com.funixproductions.api.client.twitch.reference.clients.game.TwitchGameClient;
import com.funixproductions.api.client.twitch.reference.dtos.responses.TwitchDataResponseDTO;
import com.funixproductions.api.client.twitch.reference.dtos.responses.channel.stream.TwitchStreamDTO;
import com.funixproductions.api.client.twitch.reference.dtos.responses.game.TwitchGameDTO;
import feign.FeignException;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.BotColors;
import fr.funixgaming.funixbot.core.utils.TwitchStatus;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.TwitchNotificationRole;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TwitchStreamNotifications {

    private final FunixBot funixBot;
    private final BotConfig botConfig;
    private final TwitchGameClient gameClient;
    private final TwitchStatus twitchStatus;
    private final TwitchNotificationRole twitchNotificationRole;

    private Instant lastNotificationTime;

    public TwitchStreamNotifications(FunixBot funixBot,
                                     BotConfig botConfig,
                                     TwitchGameClient gameClient,
                                     TwitchStatus twitchStatus) throws FunixBotException {
        this.funixBot = funixBot;
        this.botConfig = botConfig;
        this.gameClient = gameClient;
        this.twitchStatus = twitchStatus;

        this.twitchNotificationRole = (TwitchNotificationRole) funixBot.getRoleByName(TwitchNotificationRole.NAME);
        if (this.twitchNotificationRole == null) {
            throw new FunixBotException("The notification role for twitch is not found.");
        }
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void checkLiveStatus() {
        final TwitchStreamDTO stream = twitchStatus.getFunixStreamInfo();

        if (stream != null && (lastNotificationTime == null || lastNotificationTime.plus(12, ChronoUnit.HOURS).isBefore(Instant.now()))) {
            try {
                sendNotification(stream);
            } catch (Exception e) {
                log.error("Impossible d'envoyer une notification de live twitch. Erreur {}", e.getMessage());
            }
        }
    }

    private void sendNotification(@NonNull final TwitchStreamDTO stream) {
        final TwitchGameDTO game = getGameById(stream.getGameId());
        String gameBoxArtUrl = null;

        if (game != null) {
            gameBoxArtUrl = game.getBoxArtUrl(600, 840);
        }

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(BotColors.TWITCH_COLOR)
                .setTitle(String.format("%s est en live !", stream.getUserName()), String.format("https://twitch.tv/%s", stream.getUserName()))
                .setThumbnail(gameBoxArtUrl)
                .addField("Titre du live :", stream.getTitle(), false)
                .addField("Jeu :", stream.getGameName(), false)
                .addField("Lien :", String.format("https://twitch.tv/%s", stream.getUserName()), false)
                .setFooter("Notification de stream", funixBot.getJda().getSelfUser().getAvatarUrl());

        log.info("Envoi de la notification de stream pour {}", stream.getUserName());
        funixBot.sendChatMessage(botConfig.getTwitchChannelId(), String.format("%s est en live sur Twitch ! %s", stream.getUserName(), this.twitchNotificationRole.getRole().getAsMention()));
        funixBot.sendChatMessage(botConfig.getTwitchChannelId(), embedBuilder.build());
        lastNotificationTime = Instant.now();
    }

    @Nullable
    private TwitchGameDTO getGameById(final String gameId) {
        try {
            final TwitchDataResponseDTO<TwitchGameDTO> search = this.gameClient.getGameById(gameId);

            if (search.getData().isEmpty()) {
                return null;
            } else {
                return search.getData().get(0);
            }
        } catch (FeignException e) {
            log.error("Erreur lors de la récupération du jeu.", e);
            return null;
        }
    }

}
