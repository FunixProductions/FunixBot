package fr.funixgaming.funixbot.twitch.config;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("twitch.bot.config")
public class BotConfig {
    /**
     * The bot username used to connect to twitch chat
     */
    private String botUsername;

    /**
     * The streamer channel used to interact
     */
    private String streamerUsername;

    /**
     * Oauth token fetched from tmi app
     */
    private String botAuthToken;

    @Bean(destroyMethod = "closeConnection")
    public TwitchBot buildBot() throws FunixBotException {
        try {
            return new TwitchBot(botUsername, botAuthToken);
        } catch (TwitchIRCException e) {
            throw new FunixBotException("Erreur lors du lancement du bot twitch. IRC Error", e);
        }
    }
}
