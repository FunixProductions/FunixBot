package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.modules.BotTwitchAuth;
import fr.funixgaming.funixbot.core.modules.TwitchStreamStatus;
import fr.funixgaming.funixbot.core.utils.BotColors;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;
import fr.funixgaming.twitch.api.reference.entities.responses.channel.Stream;
import fr.funixgaming.twitch.api.reference.entities.responses.twitch.Game;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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
    private final TwitchStreamStatus streamStatus;
    private final BotTwitchAuth twitchAuth;

    private Instant lastNotificationTime;

    public TwitchStreamNotifications(FunixBot funixBot,
                                     BotConfig botConfig,
                                     TwitchStreamStatus streamStatus,
                                     BotTwitchAuth twitchAuth) {
        this.funixBot = funixBot;
        this.botConfig = botConfig;
        this.streamStatus = streamStatus;
        this.twitchAuth = twitchAuth;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void checkLiveStatus() {
        final Stream stream = streamStatus.getStream();

        if (stream != null && (lastNotificationTime == null || lastNotificationTime.plus(12, ChronoUnit.HOURS).isBefore(Instant.now()))) {
            try {
                sendNotification(stream);
            } catch (Exception e) {
                log.error("Impossible d'envoyer une notification de live twitch. Erreur {}", e.getMessage());
            }
        }
    }

    private void sendNotification(@NonNull final Stream stream) throws TwitchApiException {
        final Game game = twitchAuth.getTwitchApi().getGameById(stream.getGameId());
        String gameBoxArtUrl = null;

        if (game != null) {
            gameBoxArtUrl = game.getBoxArtUrl();
            gameBoxArtUrl = gameBoxArtUrl.replace("{width}", "600");
            gameBoxArtUrl = gameBoxArtUrl.replace("{height}", "840");
        }

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(BotColors.TWITCH_COLOR)
                .setTitle(String.format("%s est en live !", stream.getUserDisplayName()), String.format("https://twitch.tv/%s", stream.getUserName()))
                .setThumbnail(gameBoxArtUrl)
                .addField("Titre du live :", stream.getTitle(), false)
                .addField("Jeu :", stream.getGameName(), false)
                .addField("Lien :", String.format("https://twitch.tv/%s", stream.getUserName()), false)
                .setFooter("Notification de stream", funixBot.getJda().getSelfUser().getAvatarUrl());

        log.info("Envoi de la notification de stream pour {}", stream.getUserDisplayName());
        funixBot.sendChatMessage(botConfig.getTwitchChannelId(), String.format("%s est en live sur Twitch ! %s", stream.getUserDisplayName(), funixBot.getBotRoles().getTwitchNotifRole().getAsMention()));
        funixBot.sendChatMessage(botConfig.getTwitchChannelId(), embedBuilder.build());
        lastNotificationTime = Instant.now();
    }

}
