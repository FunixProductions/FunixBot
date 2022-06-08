package fr.funixgaming.funixbot.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("twitch.config")
public class TwitchConfig {
    private String streamerUsername;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String oauthCode;
}
