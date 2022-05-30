package fr.funixgaming.funixbot.twitch.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.twitch.FunixBot;
import org.springframework.stereotype.Component;

@Component
public class AutoMessages {
    private static final int LIMIT_MESSAGES = 20;

    private final FunixBot funixBot;

    private final String[] messages;
    private int count = 0;
    private int selected = 0;

    public AutoMessages(FunixBot funixBot) throws FunixBotException {
        this.funixBot = funixBot;

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
        ++this.count;

        if (this.count > LIMIT_MESSAGES) {
            this.count = 0;
            funixBot.sendChatMessage(funixBot.getBotConfig().getStreamerUsername(), messages[selected]);

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
