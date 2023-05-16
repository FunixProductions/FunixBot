package fr.funixgaming.funixbot.twitch;

import com.funixproductions.api.client.twitch.reference.clients.users.TwitchUsersClient;
import fr.funixgaming.api.client.funixbot.clients.FunixBotUserExperienceClient;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.TwitchStatus;
import fr.funixgaming.funixbot.twitch.commands.*;
import fr.funixgaming.funixbot.twitch.commands.utils.CommandHandler;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.SimpleCommand;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.funixbot.twitch.events.TwitchChatEvents;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Service
public class FunixBot {
    private final TwitchBot twitchBot;
    private final BotConfig botConfig;
    private final CommandHandler commandHandler;

    private final TwitchStatus twitchStatus;
    private final TwitchUsersClient twitchUsersClient;
    private final FunixBotUserExperienceClient userExperienceClient;

    private final Set<BotCommand> commands = new HashSet<>();

    public FunixBot(CommandHandler commandHandler,
                    TwitchChatEvents twitchChatEvents,
                    BotConfig botConfig,
                    TwitchBot twitchBot,
                    TwitchStatus twitchStatus,
                    TwitchUsersClient twitchUsersClient,
                    FunixBotUserExperienceClient userExperienceClient) throws FunixBotException {
        try {
            this.commandHandler = commandHandler;
            this.twitchBot = twitchBot;
            this.botConfig = botConfig;
            this.twitchUsersClient = twitchUsersClient;
            this.userExperienceClient = userExperienceClient;
            this.twitchStatus = twitchStatus;

            this.twitchBot.joinChannel(botConfig.getStreamerUsername());
            this.twitchBot.addEventListener(twitchChatEvents);

            configureCommands();
        } catch (FunixBotException e) {
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

            this.commandHandler.addListener(command);
        }

        this.commandHandler.addListener(new CommandGiveaway(this));
        this.commandHandler.addListener(new CommandFollowCheck(this));
        this.commandHandler.addListener(new LevelCommand(this));
        this.commandHandler.addListener(new CommandUptime(this));
        this.commandHandler.addListener(new MultiTwitchCommand(this));
    }

    public void sendChatMessage(final String channel, final String message) {
        this.twitchBot.sendMessageToChannel(channel, message);
    }

}
