package fr.funixgaming.funixbot.twitch.commands.utils;

import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.crud.enums.SearchOperation;
import feign.FeignException;
import fr.funixgaming.api.funixbot.client.clients.FunixBotCommandClient;
import fr.funixgaming.api.funixbot.client.dtos.FunixBotCommandDTO;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.tools.TwitchThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final Set<BotCommand> listeners = new HashSet<>();
    private final TwitchThreadPool threadPool = new TwitchThreadPool(4);
    private final Map<UUID, Instant> cooldownsApiCommands = new HashMap<>();

    private final FunixBotCommandClient funixBotCommandClient;
    private final TwitchBot twitchBot;

    public void addListener(final BotCommand listener) {
        listeners.add(listener);
    }

    public void onNewChat(final ChatMember member, final String message, final String channelSendMessage) {
        if (message.startsWith("!")) {
            threadPool.newTask(() -> {
                final String[] args = message.split(" ");

                if (args.length > 0 && args[0].length() > 1) {
                    final String commandName = args[0].substring(1).toLowerCase();

                    for (final BotCommand command : listeners) {
                        if (isUserEnteredCommand(commandName, command) && command.canUseCommand()) {
                            command.onUserCommand(member, commandName, Arrays.copyOfRange(args, 1, args.length));
                            return;
                        }
                    }

                    try {
                        final PageDTO<FunixBotCommandDTO> search = this.funixBotCommandClient.getAll("0", "1", String.format("command:%s:%s", SearchOperation.EQUALS.getOperation(), commandName), "");

                        if (!search.getContent().isEmpty()) {
                            final FunixBotCommandDTO commandApi = search.getContent().get(0);

                            if (canExecuteApiCommand(commandApi)) {
                                twitchBot.sendMessageToChannel(channelSendMessage, commandApi.getMessage());
                            }
                        }
                    } catch (FeignException e) {
                        log.error("Une erreur est survenue lors de la recherche de la commande ({}) twitch sur la funix api. Erreur code: {} msg: {}", commandName, e.status(), e.contentUTF8());
                    }
                }
            });
        }
    }

    private boolean canExecuteApiCommand(final FunixBotCommandDTO commandDTO) {
        final Instant now = Instant.now();
        final Instant lastExecution = cooldownsApiCommands.get(commandDTO.getId());

        if (lastExecution == null) {
            cooldownsApiCommands.put(commandDTO.getId(), now);
            return true;
        } else {
            if (now.isAfter(lastExecution.plusSeconds(3))) {
                cooldownsApiCommands.put(commandDTO.getId(), now);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isUserEnteredCommand(final String userCommand, final BotCommand command) {
        if (userCommand.equals(command.getCommandName())) {
            return true;
        }

        for (final String alias : command.getAliases()) {
            if (userCommand.equals(alias)) {
                return true;
            }
        }
        return false;
    }
}
