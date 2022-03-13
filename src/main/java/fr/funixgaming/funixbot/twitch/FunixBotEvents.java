package fr.funixgaming.funixbot.twitch;

import fr.funixgaming.twitch.api.chatbot_irc.TwitchEvents;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMessage;
import fr.funixgaming.twitch.api.chatbot_irc.events.UserChatEvent;

public class FunixBotEvents implements TwitchEvents {

    private final FunixBot bot;

    protected FunixBotEvents(final FunixBot bot) {
        this.bot = bot;
    }

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();

        bot.sendMessageToChannel("funixgaming", "COUCOU " + chatMessage.getOwner().getDisplayName());
    }
}
