package fr.funixgaming.funixbot.discord.configs;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config")
public class BotConfig {
    /**
     * Twitch Streamer username
     */
    private String streamerUsername;

    /**
     * Discord bot token get from discord app
     */
    private String botToken;

    /**
     * funix discord
     */
    private String guildId;

    private String twitchChannelId;
    private String logChannelId;
    private String generalChannelId;
    private String rolesChannelId;

    private String followerRoleId;
    private String twitchNotifRoleId;
    private String youtubeNotifRoleId;
    private String tiktokNotifRoleId;

    @Bean(destroyMethod = "shutdown")
    public JDA discordInstance() {
        try {
            final JDABuilder jdaBuilder = JDABuilder.createDefault(botToken);

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);

            jdaBuilder.setActivity(Activity.of(
                    Activity.ActivityType.WATCHING,
                    String.format("twitch.tv/%s", streamerUsername),
                    String.format("https://twitch.tv/%s", streamerUsername))
            );

            return jdaBuilder.build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException("Impossible de lancer le bot discord.", e);
        }
    }
}
