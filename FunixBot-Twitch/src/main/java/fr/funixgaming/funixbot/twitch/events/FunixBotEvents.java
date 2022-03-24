package fr.funixgaming.funixbot.twitch.events;

import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchEvents;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMessage;
import fr.funixgaming.twitch.api.chatbot_irc.events.UserChatEvent;

public class FunixBotEvents implements TwitchEvents {

    private final FunixBot bot;
    private final CommandHandler commandHandler;

    public FunixBotEvents(final FunixBot bot) {
        this.bot = bot;
        this.commandHandler = CommandHandler.getInstance();
    }

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();
        final ChatMember chatMember = event.getChatMember();
        final String message = chatMessage.getMessage();

        commandHandler.onNewChat(chatMember, message);
    }
}
