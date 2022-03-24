package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class LevelCommand extends BotCommand {
    public LevelCommand(final Bot bot) {
        super("level", "lvl", "lv");
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {

    }
}
