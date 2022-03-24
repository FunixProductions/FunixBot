package fr.funixgaming.funixbot.core;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;

public interface Bot {
    void sendChatMessage(final String channel, final String message);
    void addNewCommand(final String commandName, final BotCommand command);
    void removeCommand(final String commandName);
    void stopBot();
}
