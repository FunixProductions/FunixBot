package fr.funixgaming.funixbot.core.commands.entities;

import lombok.Getter;

@Getter
public abstract class BotCommand implements UserCommandEvent {
    private final String commandName;
    private final String[] aliases;

    public BotCommand(final String commandName, final String... aliases) {
        this.commandName = commandName.toLowerCase();
        this.aliases = new String[aliases.length];

        for (int i = 0; i < aliases.length; ++i) {
            this.aliases[i] = aliases[i].toLowerCase();
        }
    }
}
