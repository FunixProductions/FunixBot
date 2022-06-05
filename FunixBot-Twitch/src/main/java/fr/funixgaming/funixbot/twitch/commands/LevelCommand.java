package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.api.client.funixbot.dtos.FunixBotUserExperienceDTO;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.modules.ChatExperience;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LevelCommand extends BotCommand {
    private final FunixBot funixBot;

    public LevelCommand(final FunixBot bot) {
        super("level", "lvl", "lv");
        this.funixBot = bot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        try {
            final String channel = user.getChannelName();
            final FunixBotUserExperienceDTO userExperience = ChatExperience.getInstance().findExpByUserId(Integer.toString(user.getUserId()));

            if (userExperience == null) {
                funixBot.sendChatMessage(channel, String.format("%s n'a pas encore de niveau sur ce stream.", user.getDisplayName()));
            } else {
                funixBot.sendChatMessage(channel, String.format(
                        "%s %s niveau %d (%d/%d)",
                        TwitchEmotes.TWITCH_LOGO,
                        user.getDisplayName(),
                        userExperience.getLevel(),
                        userExperience.getXp(),
                        userExperience.getXpNextLevel())
                );
            }
        } catch (FunixBotException e) {
            log.error("Erreur !level command: {}", e.getMessage());
        }
    }
}
