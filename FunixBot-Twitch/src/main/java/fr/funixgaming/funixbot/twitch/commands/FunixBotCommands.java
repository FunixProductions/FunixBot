package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.core.commands.UserCommandEvent;

import java.util.HashSet;
import java.util.Set;

public class FunixBotCommands {

    private final Set<UserCommandEvent> commands = new HashSet<>();
    private final CommandHandler commandHandler = CommandHandler.getInstance();

    public FunixBotCommands() {

    }

    private void addNewCommand(final String name, final UserCommandEvent command) {
        this.commands.add(command);
        this.commandHandler.addListener(name, command);
    }

}
