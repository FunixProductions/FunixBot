package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;

public class CommandHelp extends BotCommand {
    private final Bot bot;

    public CommandHelp(Bot bot) {
        super("help");
        this.bot = bot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NonNull String[] args) {

    }
}
