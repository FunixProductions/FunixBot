package fr.funixgaming.funixbot.core;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;

import java.util.Set;

public class BotTest implements Bot {
    @Override
    public void sendChatMessage(String channel, String message) {

    }

    @Override
    public void addNewCommand(BotCommand command) {

    }

    @Override
    public void removeCommand(String commandName) {

    }

    @Override
    public void stopBot() {

    }

    @Override
    public Set<BotCommand> getCommands() {
        return null;
    }
}
