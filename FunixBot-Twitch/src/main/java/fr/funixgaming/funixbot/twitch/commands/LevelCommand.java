package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.api.client.funixbot.clients.FunixBotUserExperienceClient;
import fr.funixgaming.api.client.funixbot.dtos.FunixBotUserExperienceDTO;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.reference.TwitchApi;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LevelCommand extends BotCommand {
    private final FunixBot funixBot;

    private final TwitchApi twitchApi;
    private final FunixBotUserExperienceClient userExperienceClient;

    public LevelCommand(final FunixBot bot) {
        super("level", "lvl", "lv");
        this.funixBot = bot;

        this.twitchApi = bot.getTwitchApi();
        this.userExperienceClient = bot.getFunixBotUserExperienceClient();
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        final List<FunixBotUserExperienceDTO> experience = userExperienceClient.search(String.format("twitchUserId:%d", user.getUserId()), "0", "1");

        if (!experience.isEmpty()) {
        }
    }
}
