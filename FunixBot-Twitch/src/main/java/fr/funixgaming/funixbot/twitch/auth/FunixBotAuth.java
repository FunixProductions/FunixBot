package fr.funixgaming.funixbot.twitch.auth;

import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.exceptions.FunixBotException;
import fr.funixgaming.twitch.api.auth.TwitchAuth;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class FunixBotAuth {

    private static final File authFile = new File(FunixBot.DATA_FOLDER, "botAuth.json");
    private static volatile FunixBotAuth instance = null;

    private TwitchAuth twitchAuth;

    private FunixBotAuth() {
        new Thread(() -> {

        }).start();
    }

    public static FunixBotAuth getInstance() throws FunixBotException {
        try {
            if (instance == null) {
                final Scanner scanner = new Scanner(System.in);
                final String clientId = System.getenv("CLIENT_ID");
                final String clientSecret = System.getenv("CLIENT_SECRET");
                final String redirectUrl = System.getenv("REDIRECT_URL");

                System.out.println("Enter the bot oauthToken : ");
                final String oauthCode = scanner.nextLine();
                instance = new FunixBotAuth();

                if (authFile.exists()) {
                    final String authData = Files.readString(authFile.toPath(), StandardCharsets.UTF_8);
                    instance.twitchAuth = TwitchAuth.fromJson(authData, clientId, clientSecret);
                } else {
                    if (!authFile.createNewFile()) {
                        throw new IOException("Can't create botAuth.json");
                    }
                    instance.twitchAuth = new TwitchAuth(
                            clientId,
                            clientSecret,
                            oauthCode,
                            redirectUrl
                    );
                    saveInFile();
                }
            }

            //TODO check if keys change
            return instance;
        } catch (IOException e) {
            throw new FunixBotException("An error occurred while fetching botAuth.json", e);
        } catch (TwitchApiException twitchApiException) {
            throw new FunixBotException("An error occurred while getting twitch tokens.", twitchApiException);
        }
    }

    private static void saveInFile() throws IOException {
        Files.writeString(authFile.toPath(), instance.twitchAuth.toJson(true), StandardOpenOption.TRUNCATE_EXISTING);
    }

    public TwitchAuth getTwitchAuth() {
        return twitchAuth;
    }
}
