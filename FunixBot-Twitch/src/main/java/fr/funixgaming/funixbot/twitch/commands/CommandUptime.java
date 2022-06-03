package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;

public class CommandUptime extends BotCommand {

    private final FunixBot bot;

    public CommandUptime(final FunixBot funixBot) {
        super("uptime");
        this.bot = funixBot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NonNull String[] args) {

    }

}
