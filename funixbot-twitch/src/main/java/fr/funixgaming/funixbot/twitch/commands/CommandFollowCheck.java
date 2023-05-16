package fr.funixgaming.funixbot.twitch.commands;

import com.funixproductions.api.client.twitch.reference.dtos.responses.TwitchDataResponseDTO;
import com.funixproductions.api.client.twitch.reference.dtos.responses.user.TwitchFollowDTO;
import com.funixproductions.core.tools.time.TimeUtils;
import feign.FeignException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandFollowCheck extends BotCommand {
    private final static int COOLDOWN = 2;

    private final FunixBot bot;

    public CommandFollowCheck(final FunixBot bot) {
        super("followcheck", "fc");
        this.bot = bot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        try {
            final TwitchDataResponseDTO<TwitchFollowDTO> followDto = this.bot.getTwitchUsersClient().isUserFollowingStreamer(Integer.toString(user.getUserId()), user.getRoomID());

            if (!followDto.getData().isEmpty()) {
                final TwitchFollowDTO followDTO = followDto.getData().get(0);
                this.bot.sendChatMessage(user.getChannelName(), String.format("%s suit %s depuis %s", user.getDisplayName(), user.getChannelName(), getFollowTime(followDTO)));
            } else {
                this.bot.sendChatMessage(user.getChannelName(), String.format("%s ne suit pas %s.", user.getDisplayName(), user.getChannelName()));
            }
        } catch (FeignException e) {
            log.error("Erreur lors du follow check user {}.", user.getDisplayName(), e);
        }
    }

    private String getFollowTime(@NonNull final TwitchFollowDTO follow) {
        final Instant now = Instant.now();
        final Instant followedAt = follow.getFollowedAt().toInstant();
        final List<String> uptimeStream = new ArrayList<>();

        long durationSeconds = TimeUtils.diffBetweenInstants(followedAt, now).getSeconds();

        if (durationSeconds < 86400) {
            return "aujourd'hui";
        }

        if (durationSeconds >= 31535975) {
            final long years = durationSeconds / 31535975;
            final long secondsToRemove = 31535975 * years;

            uptimeStream.add(String.format("%d %s", years, years == 1 ? "année" : "ans"));
            durationSeconds -= secondsToRemove;
        }
        if (durationSeconds >= 2628001) {
            final long months = durationSeconds / 2628001;
            final long secondsToRemove = 2628001 * months;

            uptimeStream.add(String.format("%d mois", months));
            durationSeconds -= secondsToRemove;
        }
        if (durationSeconds >= 86400) {
            final long days = durationSeconds / 86400;

            uptimeStream.add(String.format("%d %s", days, days == 1 ? "jour" : "jours"));
        }
        return String.join(" ", uptimeStream);
    }

}
