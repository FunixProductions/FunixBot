package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.tools.TwitchThreadPool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private static final CommandHandler instance = new CommandHandler();

    private final Map<String, UserCommandEvent> listeners = new HashMap<>();
    private final TwitchThreadPool threadPool = new TwitchThreadPool(4);

    public void addListener(final String commandName, final UserCommandEvent listener) {
        listeners.put(commandName.toLowerCase(), listener);
    }

    public void onNewChat(final ChatMember member, final String message) {
        if (message.startsWith("!")) {
            threadPool.newTask(() -> {
                final String[] args = message.split(" ");

                if (args.length > 0 && args[0].length() > 1) {
                    final String commandName = args[0].substring(1).toLowerCase();

                    for (final Map.Entry<String, UserCommandEvent> eventEntry : listeners.entrySet()) {
                        if (eventEntry.getKey().equals(commandName)) {
                            final UserCommandEvent event = eventEntry.getValue();
                            event.onUserCommand(member, commandName, Arrays.copyOfRange(args, 1, args.length));
                        }
                    }
                }
            });
        }
    }

    public static CommandHandler getInstance() {
        return instance;
    }
}
