package fr.funixgaming.funixbot.core.utils;

import feign.FeignException;
import fr.funixgaming.api.client.external_api_impl.twitch.reference.clients.stream.TwitchStreamsClient;
import fr.funixgaming.api.client.external_api_impl.twitch.reference.dtos.responses.TwitchDataResponseDTO;
import fr.funixgaming.api.client.external_api_impl.twitch.reference.dtos.responses.channel.stream.TwitchStreamDTO;
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

    private final TwitchStreamsClient twitchStreamsClient;
    private TwitchStreamDTO funixStreamInfo = null;

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public void updateStreamStatus() {
        try {
            final TwitchDataResponseDTO<TwitchStreamDTO> streamDto = this.twitchStreamsClient.getFunixStream();

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
