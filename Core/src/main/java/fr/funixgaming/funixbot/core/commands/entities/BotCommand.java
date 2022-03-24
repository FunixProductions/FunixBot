package fr.funixgaming.funixbot.core.commands.entities;

import lombok.Getter;

@Getter
public abstract class BotCommand implements UserCommandEvent {
    private final String commandName;

    public BotCommand(final String commandName) {
        this.commandName = commandName;
    }
}
