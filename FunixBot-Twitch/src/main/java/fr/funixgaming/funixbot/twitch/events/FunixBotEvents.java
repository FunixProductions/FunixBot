package fr.funixgaming.funixbot.twitch.events;

import fr.funixgaming.funixbot.core.commands.CommandHandler;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.modules.AutoMessages;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchEvents;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMessage;
import fr.funixgaming.twitch.api.chatbot_irc.events.UserChatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FunixBotEvents implements TwitchEvents {

    private final CommandHandler commandHandler;
    private final AutoMessages autoMessages;

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();
        final ChatMember chatMember = event.getChatMember();
        final String message = chatMessage.getMessage();

        try {
            commandHandler.onNewChat(chatMember, message);
            autoMessages.userMessage();

            log.info("[" + chatMember.getDisplayName() + "] " + message);
        } catch (FunixBotException e) {
            e.printStackTrace();
        }
    }
}
