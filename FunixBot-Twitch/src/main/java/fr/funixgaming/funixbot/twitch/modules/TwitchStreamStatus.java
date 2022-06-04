package fr.funixgaming.funixbot.twitch.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.config.TwitchBotConfig;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;
import fr.funixgaming.twitch.api.reference.TwitchApi;
import fr.funixgaming.twitch.api.reference.entities.responses.channel.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TwitchStreamStatus {
    private static volatile TwitchStreamStatus instance;

    private final FunixBot funixBot;
    private final TwitchBotConfig botConfig;
    private final TwitchApi twitchApi;
    private Stream stream;

    public TwitchStreamStatus(FunixBot funixBot,
                              TwitchBotConfig twitchBotConfig) {
        this.funixBot = funixBot;
        this.botConfig = twitchBotConfig;
        this.twitchApi = funixBot.getTwitchApi();
        instance = this;
    }

    @Nullable
    public Stream getStream() {
        return this.stream;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void refreshStreamStatus() {
        try {
            final Set<Stream> streams = twitchApi.getStreamsByUserNames(Set.of(botConfig.getStreamerUsername()));

            if (streams.isEmpty()) {
                this.stream = null;
            } else {
                for (final Stream stream : streams) {
                    this.stream = stream;
                }
            }
        } catch (TwitchApiException e) {
            log.error("Une erreur est survenue lors du refresh du statut stream. {}", e.getMessage());
        }
    }

    public static TwitchStreamStatus getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("Twitch Stream Status pas chargé.");
        }
        return instance;
    }
}
