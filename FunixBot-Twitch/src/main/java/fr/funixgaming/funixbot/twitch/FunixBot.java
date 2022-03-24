package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.commands.FunixBotCommands;
import fr.funixgaming.funixbot.core.enums.BotProfile;
import fr.funixgaming.funixbot.twitch.config.FunixBotConfiguration;
import fr.funixgaming.funixbot.twitch.events.FunixBotEvents;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;
import lombok.Getter;

@Getter
public class FunixBot extends TwitchBot {
    private static volatile FunixBot instance = null;

    private final FunixBotConfiguration botConfiguration;
    private final FunixBotCommands funixBotCommands;

    private FunixBot(final FunixBotConfiguration botConfiguration) throws TwitchIRCException {
        super(botConfiguration.getBotProperties().getBotUsername(), botConfiguration.getTwitchAuth().getAuth());
        super.joinChannel(botConfiguration.getBotProperties().getStreamerUsername());
        super.addEventListener(new FunixBotEvents(this));

        this.botConfiguration = botConfiguration;
        this.funixBotCommands = new FunixBotCommands();
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

    public static FunixBot getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("Le bot n'est pas encore chargé.");
        }
        return instance;
    }
}
