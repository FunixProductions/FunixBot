package fr.funixgaming.funixbot.core.utils;

import com.funixproductions.api.twitch.reference.client.dtos.responses.TwitchDataResponseDTO;
import com.funixproductions.api.twitch.reference.client.dtos.responses.channel.stream.TwitchStreamDTO;
import feign.FeignException;
import fr.funixgaming.api.twitch.client.clients.TwitchStreamClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitchStatus {

    private final TwitchStreamClient twitchStreamsClient;
    private TwitchStreamDTO funixStreamInfo = null;

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public void updateStreamStatus() {
        try {
            final TwitchDataResponseDTO<TwitchStreamDTO> streamDto = this.twitchStreamsClient.getStream();

            if (streamDto.getData().isEmpty()) {
                this.funixStreamInfo = null;
            } else {
                this.funixStreamInfo = streamDto.getData().get(0);
            }
        } catch (FeignException e) {
            this.funixStreamInfo = null;
            log.error("Impossible de récupérer le statut du stream de funixgaming.", e);
        }
    }

    @Nullable
    public TwitchStreamDTO getFunixStreamInfo() {
        return funixStreamInfo;
    }
}
