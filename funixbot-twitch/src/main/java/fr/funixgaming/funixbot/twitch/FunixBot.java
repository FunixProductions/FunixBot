package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.api.client.external_api_impl.twitch.reference.clients.users.TwitchUsersClient;
import fr.funixgaming.api.client.funixbot.clients.FunixBotUserExperienceClient;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.TwitchStatus;
import fr.funixgaming.funixbot.twitch.commands.*;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.SimpleCommand;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.funixbot.twitch.events.ChatMessagesEvent;
import fr.funixgaming.funixbot.twitch.services.CommandHandlerService;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Getter
@Service
public class FunixBot {
    private final TwitchBot twitchBot;
    private final BotConfig botConfig;

    private final CommandHandlerService commandHandlerService;

    private final TwitchStatus twitchStatus;
    private final TwitchUsersClient twitchUsersClient;
    private final FunixBotUserExperienceClient userExperienceClient;

    public FunixBot(CommandHandlerService commandHandlerService,
                    ChatMessagesEvent chatMessagesEvent,
                    BotConfig botConfig,
                    TwitchBot twitchBot,
                    TwitchStatus twitchStatus,
                    TwitchUsersClient twitchUsersClient,
                    FunixBotUserExperienceClient userExperienceClient) throws FunixBotException {
        try {
            this.commandHandlerService = commandHandlerService;
            this.twitchBot = twitchBot;
            this.botConfig = botConfig;
            this.twitchUsersClient = twitchUsersClient;
            this.userExperienceClient = userExperienceClient;
            this.twitchStatus = twitchStatus;

            this.twitchBot.joinChannel(botConfig.getStreamerUsername());
            this.twitchBot.addEventListener(chatMessagesEvent);

            configureResourceCommands();
            configureCommands();
        } catch (FunixBotException e) {
            log.error("Une erreur est survenue lors du start du bot twitch. Erreur: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sendChatMessage(final String channel, final String message) {
        this.twitchBot.sendMessageToChannel(channel, message);
    }

    private void configureCommands() throws FunixBotException {
        this.commandHandlerService.addListener(new CommandGiveaway(this));
        this.commandHandlerService.addListener(new CommandFollowCheck(this));
        this.commandHandlerService.addListener(new LevelCommand(this));
        this.commandHandlerService.addListener(new CommandUptime(this));
        this.commandHandlerService.addListener(new MultiTwitchCommand(this));
    }

    private void configureResourceCommands() throws FunixBotException {
        final String channelToSend = botConfig.getStreamerUsername();
        final Set<SimpleCommand> simpleCommands = SimpleCommand.getCommandsFromClasspath();

        for (final SimpleCommand command : simpleCommands) {
            command.setChannelToSend(channelToSend);
            command.setBot(this);

            this.commandHandlerService.addListener(command);
        }
    }

}
