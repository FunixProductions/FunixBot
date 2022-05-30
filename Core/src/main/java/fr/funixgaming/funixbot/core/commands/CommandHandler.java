package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.tools.TwitchThreadPool;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandHandler {
    private final Set<BotCommand> listeners = new HashSet<>();
    private final TwitchThreadPool threadPool = new TwitchThreadPool(4);

    public void addListener(final BotCommand listener) {
        listeners.add(listener);
    }

    public void onNewChat(final ChatMember member, final String message) {
        if (message.startsWith("!")) {
            threadPool.newTask(() -> {
                final String[] args = message.split(" ");

                if (args.length > 0 && args[0].length() > 1) {
                    final String commandName = args[0].substring(1).toLowerCase();

                    for (final BotCommand command : listeners) {
                        if (isUserEnteredCommand(commandName, command)) {
                            command.onUserCommand(member, commandName, Arrays.copyOfRange(args, 1, args.length));
                        }
                    }
                }
            });
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
