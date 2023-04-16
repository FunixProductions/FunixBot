package fr.funixgaming.funixbot.twitch.events;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.commands.CommandGiveaway;
import fr.funixgaming.funixbot.twitch.commands.utils.CommandHandler;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.funixbot.twitch.modules.AutoMessages;
import fr.funixgaming.funixbot.twitch.modules.ChatExperience;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
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
public class TwitchChatEvents implements TwitchEvents {

    private final CommandHandler commandHandler;
    private final AutoMessages autoMessages;
    private final ChatExperience chatExperience;
    private final TwitchBot twitchBot;
    private final BotConfig botConfig;

    @Override
    public void onUserChat(UserChatEvent event) {
        final ChatMessage chatMessage = event.getChatMessage();
        final ChatMember chatMember = event.getChatMember();
        final String message = chatMessage.getMessage();

        try {
            commandHandler.onNewChat(chatMember, message, chatMember.getChannelName());
            chatExperience.userChatExp(chatMember);
            CommandGiveaway.getInstance().onUserChat(chatMessage);

            if (!message.startsWith("!")) {
                autoMessages.userMessage();
                handleFeur(message);
            }

            log.info("[" + chatMember.getDisplayName() + "] " + message);
        } catch (FunixBotException e) {
            log.error("Erreur chat event: {}", e.getMessage());
        }
    }

    private void handleFeur(String message) {
        
        String[] quoiList = {
                "quoi",
                "koi"
        };
        
        for (String quoi : quoiList) {
            if (message.endsWith(quoi) || message.endsWith(quoi + "?")) {
                twitchBot.sendMessageToChannel(botConfig.getStreamerUsername(), "feur");
            }
        }
    }
}
