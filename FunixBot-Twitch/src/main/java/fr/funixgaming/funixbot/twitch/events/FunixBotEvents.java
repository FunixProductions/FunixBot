package fr.funixgaming.funixbot.twitch.events;

import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.FunixBotLog;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.modules.AutoMessages;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchEvents;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMessage;
import fr.funixgaming.twitch.api.chatbot_irc.events.UserChatEvent;

public class FunixBotEvents implements TwitchEvents {

    private final CommandHandler commandHandler = CommandHandler.getInstance();
    private final FunixBotLog log = FunixBotLog.getInstance();
    private final AutoMessages autoMessages;
    private final FunixBot bot;

    public FunixBotEvents(final FunixBot bot) throws FunixBotException {
        this.bot = bot;
        this.autoMessages = new AutoMessages();
    }

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();
        final ChatMember chatMember = event.getChatMember();
        final String message = chatMessage.getMessage();

        try {
            commandHandler.onNewChat(chatMember, message);
            autoMessages.userMessage();
            log.logInfo("[" + chatMember.getDisplayName() + "] " + message);
        } catch (FunixBotException e) {
            e.printStackTrace();
        }
    }
}
