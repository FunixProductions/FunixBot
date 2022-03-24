package fr.funixgaming.funixbot.core.commands.entities;

import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class SimpleCommand extends BotCommand {
    private final Bot bot;
    private final String response;
    private final String channelToSend;

    public SimpleCommand(final String commandName, final Bot bot, final String response, final String channelResponse) {
        super(commandName);
        this.bot = bot;
        this.response = response;
        this.channelToSend = channelResponse;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        bot.sendChatMessage(channelToSend, response);
    }
}
