package fr.funixgaming.funixbot.core.commands;

import feign.FeignException;
import fr.funixgaming.api.client.funixbot.clients.FunixBotCommandClient;
import fr.funixgaming.api.client.funixbot.dtos.FunixBotCommandDTO;
import fr.funixgaming.api.core.crud.enums.SearchOperation;
import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.tools.TwitchThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    public void addListener(final BotCommand listener) {
        listeners.add(listener);
    }

    public void onNewChat(final ChatMember member, final String message, final Bot bot, final String channelSendMessage) {
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
                        final Page<FunixBotCommandDTO> search = this.funixBotCommandClient.getAll("0", "1", String.format("command:%s:%s", SearchOperation.EQUALS, commandName), "");

                        if (!search.isEmpty()) {
                            final FunixBotCommandDTO commandApi = search.getContent().get(0);

                            if (canExcecuteApiCommand(commandApi)) {
                                bot.sendChatMessage(channelSendMessage, commandApi.getMessage());
                            }
                        }
                    } catch (FeignException e) {
                        log.error("Une erreur est survenue lors de la recherche de la commande ({}) twitch sur la funix api. Erreur code: {} msg: {}", commandName, e.status(), e.contentUTF8());
                    }
                }
            });
        }
    }

    private boolean canExcecuteApiCommand(final FunixBotCommandDTO commandDTO) {
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
