package fr.funixgaming.funixbot.core.commands.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SimpleCommand extends BotCommand {
    private final Bot bot;
    private final String response;
    private final String channelToSend;

    public SimpleCommand(final Bot bot, final String commandName, final String response, final String channelResponse) {
        super(commandName);
        this.bot = bot;
        this.response = response;
        this.channelToSend = channelResponse;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        bot.sendChatMessage(channelToSend, response);
    }

    /**
     * @return Map -> command name, command response
     */
    public static Map<String, String> getCommandsFromClasspath() throws FunixBotException {
        final String read = DataFiles.readFileFromClasspath("/commands.json");
        final JsonObject json = JsonParser.parseString(read).getAsJsonObject();
        final JsonArray data = json.get("data").getAsJsonArray();

        final Map<String, String> commands = new HashMap<>();
        for (final JsonElement element : data) {
            final JsonObject command = element.getAsJsonObject();
            commands.put(command.get("command").getAsString(), command.get("response").getAsString());
        }
        return commands;
    }
}
