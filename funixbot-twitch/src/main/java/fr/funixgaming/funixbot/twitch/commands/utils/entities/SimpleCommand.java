package fr.funixgaming.funixbot.twitch.commands.utils.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SimpleCommand extends BotCommand {
    private final String response;
    private FunixBot bot;
    private String channelToSend;

    public SimpleCommand(final String response,
                         final String commandName,
                         final String... aliases) {
        super(commandName, aliases);
        this.response = response;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        bot.sendChatMessage(channelToSend, response);
    }

    /**
     * @return Map -> command name, command response
     */
    public static Set<SimpleCommand> getCommandsFromClasspath() throws FunixBotException {
        final String read = DataFiles.readFileFromClasspath("/json/commands.json");
        final JsonObject json = JsonParser.parseString(read).getAsJsonObject();
        final JsonArray data = json.get("data").getAsJsonArray();
        final Set<SimpleCommand> commands = new HashSet<>();

        for (final JsonElement element : data) {
            final JsonObject commandData = element.getAsJsonObject();
            final String command = commandData.get("command").getAsString();
            final String response = commandData.get("response").getAsString();
            final JsonElement elemAlias = commandData.get("aliases");
            final String[] aliases;

            if (elemAlias == null || elemAlias.isJsonNull()) {
                aliases = new String[0];
            } else {
                final JsonArray aliasList = elemAlias.getAsJsonArray();
                final int size = aliasList.size();

                aliases = new String[size];
                for (int i = 0; i < size; ++i) {
                    aliases[i] = aliasList.get(i).getAsString();
                }
            }
            commands.add(new SimpleCommand(response, command, aliases));
        }
        return commands;
    }
}
