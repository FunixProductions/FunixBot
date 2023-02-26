package fr.funixgaming.funixbot.twitch.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class AutoMessagesService {
    private static final int LIMIT_MESSAGES = 20;

    private final TwitchBot twitchBot;
    private final BotConfig botConfig;

    private final String[] messages;
    private int count = 0;
    private int selected = 0;
    private Instant lastMessageTime = Instant.now();

    public AutoMessagesService(TwitchBot twitchBot,
                               BotConfig botConfig) throws FunixBotException {
        this.twitchBot = twitchBot;
        this.botConfig = botConfig;

        final String data = DataFiles.readFileFromClasspath("/json/autoMessages.json");
        final JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
        final JsonArray array = obj.get("messages").getAsJsonArray();
        final int size = array.size();

        this.messages = new String[size];
        for (int i = 0; i < size; ++i) {
            this.messages[i] = array.get(i).getAsString();
        }
    }

    public void userMessage() throws FunixBotException {
        final Instant now = Instant.now();

        ++this.count;

        if (this.count > LIMIT_MESSAGES && lastMessageTime.plus(10, ChronoUnit.MINUTES).isBefore(now)) {
            this.lastMessageTime = now;
            this.count = 0;

            twitchBot.sendMessageToChannel(botConfig.getStreamerUsername(), messages[selected]);

            ++this.selected;
            if (this.selected >= this.messages.length) {
                this.selected = 0;
            }
        }
    }

    public String[] getMessages() {
        return messages;
    }
}
