package fr.funixgaming.funixbot.core.twitch.auth;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.twitch.api.auth.TwitchAuth;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class BotTwitchAuth {
    private static final File authFile = new File(DataFiles.getInstance().getDataFolder(), "botTwitchAuth.json");

    private final TwitchAuth auth;
    private final Thread checkThread;
    private boolean working = true;

    public BotTwitchAuth(final String clientId,
                         final String clientSecret,
                         final String redirectUrl,
                         final String oauthCode) throws FunixBotException {
        if (clientId == null || clientSecret == null || redirectUrl == null || oauthCode == null) {
            throw new FunixBotException("Vous n'avez pas spécifié dans les variables d'env les tokens twitch. TWITCH_CLIENT_ID TWITCH_CLIENT_SECRET TWITCH_REDIRECT_URL TWITCH_OAUTH_CODE");
        }

        try {
            if (authFile.exists()) {
                final String authData = Files.readString(authFile.toPath(), StandardCharsets.UTF_8);
                this.auth = TwitchAuth.fromJson(authData, clientId, clientSecret);
            } else {
                if (!authFile.createNewFile()) {
                    throw new IOException("Can't create botAuth.json");
                }
                this.auth = new TwitchAuth(
                        clientId,
                        clientSecret,
                        oauthCode,
                        redirectUrl
                );
                saveInFile();
            }
        } catch (IOException e) {
            throw new FunixBotException("Une erreur est survenue lors de la lecture/écriture des fichiers.", e);
        } catch (TwitchApiException e) {
            throw new FunixBotException("Une erreur est survenue lors de la connecxion à l'api twitch.", e);
        }

        this.checkThread = new Thread(() -> {
            while (this.working) {
                try {
                    this.auth.isUsable();
                    if (!this.auth.isValid()) {
                        this.auth.refresh();
                        saveInFile();
                    }

                    Thread.sleep(20000);
                } catch (InterruptedException ignored) {
                } catch (TwitchApiException | IOException e) {
                    log.error("Erreur lors ", e);
                }
            }
        });
        this.checkThread.start();
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
