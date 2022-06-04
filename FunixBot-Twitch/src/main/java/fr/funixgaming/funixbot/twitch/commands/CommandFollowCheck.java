package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.api.core.utils.time.TimeUtils;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.reference.TwitchApi;
import fr.funixgaming.twitch.api.reference.entities.responses.twitch.Follow;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
public class CommandFollowCheck extends BotCommand {
    private final static int COOLDOWN = 2;

    private final FunixBot bot;
    private final TwitchApi twitchApi;

    private final Queue<ChatMember> followCheckQueue = new LinkedList<>();

    public CommandFollowCheck(final FunixBot bot) {
        super("followcheck", "fc");
        this.bot = bot;
        this.twitchApi = bot.getTwitchApi();

        new Thread(this::processCommands).start();
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        for (final ChatMember chatMember : followCheckQueue) {
            if (chatMember.getUserId() == user.getUserId()) {
                return;
            }
        }

        this.followCheckQueue.add(user);
    }

    private void processCommands() {
        try {
            while (bot.isRunning()) {
                final ChatMember user = this.followCheckQueue.poll();

                if (user != null) {
                    final Follow follow = this.twitchApi.isUserFollowing(user.getRoomID(), Integer.toString(user.getUserId()));

                    if (follow == null) {
                        this.bot.sendChatMessage(user.getChannelName(), String.format("%s ne suit pas %s.", user.getDisplayName(), user.getChannelName()));
                    } else {
                        this.bot.sendChatMessage(user.getChannelName(), String.format("%s suit %s depuis %s", user.getDisplayName(), user.getChannelName(), getFollowTime(follow)));
                    }
                }
                Thread.sleep(COOLDOWN * 1000);
            }
        } catch (Exception e) {
            log.error("Erreur lors du process des follow check {}", e.getMessage());
        }
    }

    private String getFollowTime(@NonNull final Follow follow) {
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
