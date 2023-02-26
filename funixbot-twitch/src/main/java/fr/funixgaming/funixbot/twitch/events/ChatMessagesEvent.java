package fr.funixgaming.funixbot.twitch.events;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.commands.CommandGiveaway;
import fr.funixgaming.funixbot.twitch.services.AutoMessagesService;
import fr.funixgaming.funixbot.twitch.services.ChatExperienceService;
import fr.funixgaming.funixbot.twitch.services.CommandHandlerService;
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
public class ChatMessagesEvent implements TwitchEvents {

    private final CommandHandlerService commandHandlerService;
    private final AutoMessagesService autoMessagesService;
    private final ChatExperienceService chatExperienceService;

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();
        final ChatMember chatMember = event.getChatMember();
        final String message = chatMessage.getMessage();

        try {
            commandHandlerService.onNewChat(chatMember, message, chatMember.getChannelName());
            chatExperienceService.userChatExp(chatMember);
            CommandGiveaway.getInstance().onUserChat(chatMessage);

            if (!message.startsWith("!")) {
                autoMessagesService.userMessage();
            }

            log.info("[{}] {}", chatMember.getDisplayName(), message);
        } catch (FunixBotException e) {
            log.error("Erreur chat event: {}", e.getMessage());
        }
    }
}
