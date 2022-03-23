package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.funixbot.core.enums.BotProfile;
import fr.funixgaming.funixbot.twitch.config.FunixBotConfiguration;
import fr.funixgaming.funixbot.twitch.events.FunixBotEvents;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;

public class FunixBot extends TwitchBot {
    private static volatile FunixBot instance = null;

    private final FunixBotConfiguration botConfiguration;

    private FunixBot(final FunixBotConfiguration botConfiguration) throws TwitchIRCException {
        super(botConfiguration.getBotProperties().getBotUsername(), botConfiguration.getTwitchAuth().getAuth());

        this.botConfiguration = botConfiguration;
        super.joinChannel(this.botConfiguration.getBotProperties().getStreamerUsername());
        super.addEventListener(new FunixBotEvents(this));
    }

    public void stopBot() {
        this.botConfiguration.getTwitchAuth().stop();
        super.closeConnection();
    }

    /**
     * @param args Program arguments --profile={@link BotProfile} check enum, if not specified or invalid it will use production mode<br/>
     */
    public static void main(final String[] args) {
        try {
            FunixBotConfiguration.init(args);

            instance = new FunixBot(FunixBotConfiguration.getInstance());
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
