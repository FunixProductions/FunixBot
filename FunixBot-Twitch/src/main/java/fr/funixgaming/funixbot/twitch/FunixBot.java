package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.core.commands.entities.SimpleCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.enums.BotProfile;
import fr.funixgaming.funixbot.twitch.commands.CommandGiveaway;
import fr.funixgaming.funixbot.core.commands.entities.StaticCommand;
import fr.funixgaming.funixbot.twitch.commands.CommandHelp;
import fr.funixgaming.funixbot.twitch.config.FunixBotConfiguration;
import fr.funixgaming.funixbot.twitch.events.FunixBotEvents;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class FunixBot extends TwitchBot implements Bot {
    private static volatile FunixBot instance = null;

    private final CommandHandler commandHandler = CommandHandler.getInstance();
    private final Set<BotCommand> commands = new HashSet<>();

    private final FunixBotConfiguration botConfiguration;

    private FunixBot(final FunixBotConfiguration botConfiguration) throws TwitchIRCException, FunixBotException {
        super(botConfiguration.getBotProperties().getBotUsername(), botConfiguration.getTwitchAuth().getAuth());
        super.joinChannel(botConfiguration.getBotProperties().getStreamerUsername());
        super.addEventListener(new FunixBotEvents(this));

        this.botConfiguration = botConfiguration;
        configureCommands();
    }

    private void configureCommands() throws FunixBotException {
        final String channelToSend = botConfiguration.getBotProperties().getStreamerUsername();
        final Map<String, String> simpleCommands = SimpleCommand.getCommandsFromClasspath();

        for (final Map.Entry<String, String> command : simpleCommands.entrySet()) {
            final String commandName = command.getKey();
            final String response = command.getValue();

            addNewCommand(new SimpleCommand(this, commandName, response, channelToSend));
        }

        addNewCommand(new CommandGiveaway(this));
        addNewCommand(new CommandHelp(this));
    }

    @Override
    public void sendChatMessage(final String channel, final String message) {
        super.sendMessageToChannel(channel, message);
    }

    @Override
    public void addNewCommand(final BotCommand command) {
        this.commands.add(command);
        this.commandHandler.addListener(command);
    }

    @Override
    public void removeCommand(final String commandName) {
        this.commands.removeIf(command -> !(command instanceof StaticCommand) && command.getCommandName().equalsIgnoreCase(commandName));
    }

    @Override
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
            throw new FunixBotException("Le bot twitch n'est pas encore chargé.");
        }
        return instance;
    }
}
