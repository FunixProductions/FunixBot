package fr.funixgaming.funixbot.core.modules;

import fr.funixgaming.funixbot.core.configs.TwitchConfig;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.twitch.api.auth.TwitchAuth;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;
import fr.funixgaming.twitch.api.reference.TwitchApi;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
@Getter
@Component
public class BotTwitchAuth {
    private static final File authFile = new File(DataFiles.getInstance().getDataFolder(), "botTwitchAuth.json");

    private final TwitchAuth auth;
    private final Thread checkThread;
    private final TwitchApi twitchApi;
    private boolean working = true;

    public BotTwitchAuth(final TwitchConfig twitchConfig) throws FunixBotException {
        if (twitchConfig.getClientId() == null ||
                twitchConfig.getClientSecret() == null ||
                twitchConfig.getRedirectUrl() == null ||
                twitchConfig.getOauthCode() == null) {
            throw new FunixBotException("Vous n'avez pas spécifié dans les variables d'env les tokens twitch. TWITCH_CLIENT_ID TWITCH_CLIENT_SECRET TWITCH_REDIRECT_URL TWITCH_OAUTH_CODE");
        }

        try {
            if (authFile.exists()) {
                final String authData = Files.readString(authFile.toPath(), StandardCharsets.UTF_8);
                this.auth = TwitchAuth.fromJson(authData, twitchConfig.getClientId(), twitchConfig.getClientSecret());
            } else {
                if (!authFile.createNewFile()) {
                    throw new IOException("Can't create botAuth.json");
                }
                this.auth = new TwitchAuth(
                        twitchConfig.getClientId(),
                        twitchConfig.getClientSecret(),
                        twitchConfig.getOauthCode(),
                        twitchConfig.getRedirectUrl()
                );
                saveInFile();
            }
        } catch (IOException e) {
            throw new FunixBotException("Une erreur est survenue lors de la lecture/écriture des fichiers.", e);
        } catch (TwitchApiException e) {
            throw new FunixBotException("Une erreur est survenue lors de la connexion à l'api twitch.", e);
        }

        this.checkThread = new Thread(() -> {
            while (this.working) {
                try {
                    this.auth.isUsable();
                    if (!this.auth.isValid()) {
                        this.auth.refresh();
                    }

                    saveInFile();
                    Thread.sleep(20000);
                } catch (InterruptedException ignored) {
                } catch (TwitchApiException | IOException e) {
                    log.error("Erreur lors du refresh token", e);
                }
            }
        });
        this.checkThread.start();
        this.twitchApi = new TwitchApi(this.auth);
    }

    public void stop() {
        this.working = false;
        this.checkThread.interrupt();
    }

    public TwitchAuth getAuth() {
        return auth;
    }

    private void saveInFile() throws IOException {
        DataFiles.setInFile(authFile, this.auth.toJson(true));
    }
}
