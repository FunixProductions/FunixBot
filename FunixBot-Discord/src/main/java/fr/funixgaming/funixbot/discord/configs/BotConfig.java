package fr.funixgaming.funixbot.discord.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config")
public class BotConfig {
    private String botToken;

    private String twitchChannelId;
    private String logChannelId;
    private String twitchRoleNotificationId;
}
