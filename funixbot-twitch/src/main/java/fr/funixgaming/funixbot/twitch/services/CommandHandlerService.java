package fr.funixgaming.funixbot.twitch.services;

import com.google.common.base.Strings;
import feign.FeignException;
import fr.funixgaming.api.client.funixbot.clients.FunixBotCommandClient;
import fr.funixgaming.api.client.funixbot.dtos.FunixBotCommandDTO;
import fr.funixgaming.api.core.crud.dtos.PageDTO;
import fr.funixgaming.api.core.crud.enums.SearchOperation;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandHandlerService {
    private static final String COMMAND_PREFIX = "!";

    private final Set<BotCommand> listeners = new HashSet<>();
    private final Map<UUID, Instant> cooldownsApiCommands = new HashMap<>();

    private final FunixBotCommandClient funixBotCommandClient;
    private final TwitchBot twitchBot;

    public void addListener(final BotCommand listener) {
        listeners.add(listener);
    }

    public void onNewChat(final ChatMember member, final String message, final String channelSendMessage) {
        if (!message.startsWith(COMMAND_PREFIX)) return;

        final String[] args = message.split(" ");
        if (args.length == 0 || Strings.isNullOrEmpty(args[0])) return;

        final String commandName = args[0].substring(1).toLowerCase();
        for (final BotCommand command : listeners) {
            if (command.equals(commandName) && command.canUseCommand()) {
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
}
