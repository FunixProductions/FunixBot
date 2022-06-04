package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.api.client.funixbot.clients.FunixBotCommandClient;
import fr.funixgaming.api.client.funixbot.clients.FunixBotUserExperienceClient;
import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.core.commands.entities.SimpleCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.twitch.auth.BotTwitchAuth;
import fr.funixgaming.funixbot.twitch.commands.CommandFollowCheck;
import fr.funixgaming.funixbot.twitch.commands.CommandGiveaway;
import fr.funixgaming.funixbot.core.commands.entities.StaticCommand;
import fr.funixgaming.funixbot.twitch.commands.CommandUptime;
import fr.funixgaming.funixbot.twitch.commands.LevelCommand;
import fr.funixgaming.funixbot.twitch.config.TwitchBotConfig;
import fr.funixgaming.funixbot.twitch.events.FunixBotEvents;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.exceptions.TwitchIRCException;
import fr.funixgaming.twitch.api.reference.TwitchApi;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Service
public class FunixBot implements Bot, ServletContextListener {
    private static volatile FunixBot instance = null;

    private final TwitchBot twitchBot;
    private final BotTwitchAuth botTwitchAuth;
    private final TwitchBotConfig botConfig;
    private final TwitchApi twitchApi;
    private final CommandHandler commandHandler;

    private final FunixBotCommandClient funixBotCommandClient;
    private final FunixBotUserExperienceClient funixBotUserExperienceClient;

    private final Set<BotCommand> commands = new HashSet<>();

    private boolean running = true;

    public FunixBot(TwitchBotConfig botConfig,
                    CommandHandler commandHandler,
                    FunixBotEvents funixBotEvents,
                    FunixBotCommandClient funixBotCommandClient,
                    FunixBotUserExperienceClient funixBotUserExperienceClient) throws FunixBotException, TwitchIRCException {
        try {
            this.botConfig = botConfig;
            this.commandHandler = commandHandler;
            this.funixBotCommandClient = funixBotCommandClient;
            this.funixBotUserExperienceClient = funixBotUserExperienceClient;

            this.botTwitchAuth = new BotTwitchAuth(botConfig.getClientId(), botConfig.getClientSecret(), botConfig.getRedirectUrl(), botConfig.getOauthCode());
            this.twitchBot = new TwitchBot(botConfig.getBotUsername(), this.botTwitchAuth.getAuth());
            this.twitchApi = new TwitchApi(this.botTwitchAuth.getAuth());

            this.twitchBot.joinChannel(botConfig.getStreamerUsername());
            this.twitchBot.addEventListener(funixBotEvents);

            configureCommands();

            instance = this;
        } catch (FunixBotException | TwitchIRCException e) {
            log.error("Une erreur est survenue lors du start du bot twitch. Erreur: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void configureCommands() throws FunixBotException {
        final String channelToSend = botConfig.getStreamerUsername();
        final Set<SimpleCommand> simpleCommands = SimpleCommand.getCommandsFromClasspath();

        for (final SimpleCommand command : simpleCommands) {
            command.setChannelToSend(channelToSend);
            command.setBot(this);

            addNewCommand(command);
        }

        addNewCommand(new CommandGiveaway(this));
        addNewCommand(new CommandFollowCheck(this));
        addNewCommand(new LevelCommand(this));
        addNewCommand(new CommandUptime(this));
    }

    @Override
    public void sendChatMessage(final String channel, final String message) {
        this.twitchBot.sendMessageToChannel(channel, message);
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
        this.running = false;
        this.botTwitchAuth.stop();
        this.twitchBot.closeConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.stopBot();
    }

    public static FunixBot getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("Le bot n'est pas encore chargé.");
        } else {
            return instance;
        }
    }
}
