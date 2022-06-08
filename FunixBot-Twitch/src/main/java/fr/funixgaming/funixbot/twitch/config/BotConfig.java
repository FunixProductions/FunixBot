package fr.funixgaming.funixbot.twitch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("twitch.bot.config")
public class BotConfig {
    private String botUsername;
}
