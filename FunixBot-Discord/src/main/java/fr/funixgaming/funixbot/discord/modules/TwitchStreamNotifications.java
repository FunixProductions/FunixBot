package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.modules.TwitchStreamStatus;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.twitch.api.reference.entities.responses.channel.Stream;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class TwitchStreamNotifications {

    private final FunixBot funixBot;
    private final BotConfig botConfig;
    private final TwitchStreamStatus streamStatus;

    private Instant lastNotificationTime;

    public TwitchStreamNotifications(FunixBot funixBot,
                                     BotConfig botConfig,
                                     TwitchStreamStatus streamStatus) {
        this.funixBot = funixBot;
        this.botConfig = botConfig;
        this.streamStatus = streamStatus;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void checkLiveStatus() {
        final Stream stream = streamStatus.getStream();
    }

    private void sendNotification(@NonNull final Stream stream) {

    }

}
