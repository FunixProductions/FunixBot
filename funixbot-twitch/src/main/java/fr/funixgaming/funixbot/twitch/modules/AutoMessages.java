package fr.funixgaming.funixbot.twitch.modules;

import com.funixproductions.api.twitch.reference.client.dtos.responses.channel.stream.TwitchStreamDTO;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.funixgaming.api.funixbot.client.clients.FunixBotAutoMessagesClient;
import fr.funixgaming.api.funixbot.client.dtos.FunixBotAutoMessageDTO;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.core.utils.TwitchStatus;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoMessages {
    private static final int LIMIT_MESSAGES = 20;
    private static final int MESSAGE_COOLDOWN = 5;

    private final TwitchBot twitchBot;
    private final BotConfig botConfig;
    private final FunixBotAutoMessagesClient funixBotAutoMessagesClient;
    private final TwitchStatus twitchStatus;

    private int count = 0;
    private int selected = 0;
    private Instant lastMessageTime = Instant.now();

    public void userMessage() throws FunixBotException {
        final Instant now = Instant.now();

        ++this.count;

        if (this.count > LIMIT_MESSAGES && lastMessageTime.plus(MESSAGE_COOLDOWN, ChronoUnit.MINUTES).isBefore(now)) {
            final List<FunixBotAutoMessageDTO> messages = this.funixBotAutoMessagesClient.getAll("0", "1000", null, null).getContent();
            if (messages.isEmpty()) {
                return;
            }
            final int messagesSize = messages.size();

            this.lastMessageTime = now;
            this.count = 0;
            if (this.selected >= messagesSize) {
                this.selected = 0;
            }

            final FunixBotAutoMessageDTO message = messages.get(this.selected);
            this.checkIfMessageIsNotStreamGameSpecific(message, messagesSize);
            this.twitchBot.sendMessageToChannel(botConfig.getStreamerUsername(), message.getMessage());
            this.selected++;
        }
    }

    private void checkIfMessageIsNotStreamGameSpecific(final FunixBotAutoMessageDTO message, final int messagesSize) {
        final TwitchStreamDTO twitchStatus = this.twitchStatus.getFunixStreamInfo();

        if (twitchStatus == null) {
            if (!Strings.isNullOrEmpty(message.getGameName())) {
                this.selected++;
            }
        } else {
            if (!Strings.isNullOrEmpty(message.getGameName()) && !message.getGameName().equalsIgnoreCase(twitchStatus.getGameName())) {
                this.selected++;
            }
        }

        if (this.selected >= messagesSize) {
            this.selected = 0;
        }
    }

}
