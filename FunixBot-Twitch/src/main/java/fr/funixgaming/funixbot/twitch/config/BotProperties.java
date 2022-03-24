package fr.funixgaming.funixbot.twitch.config;

import fr.funixgaming.funixbot.core.enums.BotProfile;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class BotProperties {
    private final String botUsername;
    private final String streamerUsername;

    protected BotProperties(final BotProfile profile) throws FunixBotException {
        final String nameFileProperties;

        if (profile == BotProfile.LOCAL) {
            nameFileProperties = "bot.local.properties";
        } else {
            nameFileProperties = "bot.prod.properties";
        }

        final InputStream inputStream = FunixBot.class.getResourceAsStream('/' + nameFileProperties);
        if (inputStream == null) {
            throw new FunixBotException("Le ficher classpath " + nameFileProperties + " est introuvable.");
        }

        try {
            final Properties properties = new Properties();
            properties.load(inputStream);

            this.botUsername = properties.get("botUsername").toString();
            this.streamerUsername = properties.get("streamerUsername").toString();
        } catch (IOException e) {
            throw new FunixBotException("Une erreur est survenue lors de la lecture des fichers de properties.", e);
        } catch (NullPointerException e) {
            throw new FunixBotException("Les fichiers properties sont invalides.", e);
        }
    }

}
