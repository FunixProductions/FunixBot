package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class CommandFollowCheck extends BotCommand {

    private final FunixBot bot;

    public CommandFollowCheck(final FunixBot bot) {
        super("followcheck", "fc");
        this.bot = bot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {

    }
}
