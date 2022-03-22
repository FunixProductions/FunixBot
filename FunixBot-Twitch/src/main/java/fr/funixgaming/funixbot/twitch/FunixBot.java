package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.funixbot.twitch.auth.FunixBotAuth;
import fr.funixgaming.funixbot.twitch.events.FunixBotEvents;
import fr.funixgaming.funixbot.twitch.exceptions.FunixBotException;
import fr.funixgaming.twitch.api.auth.TwitchAuth;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;

import java.io.File;

public class FunixBot extends TwitchBot {
    public static final File DATA_FOLDER = new File("data");
    private static volatile FunixBot instance = null;

    private FunixBot(final TwitchAuth auth) throws TwitchIRCException {
        super("testfunix", auth);
        super.joinChannel("funixgaming");
        super.addEventListener(new FunixBotEvents(this));
    }

    public static void main(final String[] args) {
        try {
            if (!DATA_FOLDER.exists() && !DATA_FOLDER.mkdir()) {
                throw new FunixBotException("The data folder can't be created.");
            }

            instance = new FunixBot(FunixBotAuth.getInstance().getTwitchAuth());
            while (instance.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static FunixBot getInstance() {
        return instance;
    }
}
