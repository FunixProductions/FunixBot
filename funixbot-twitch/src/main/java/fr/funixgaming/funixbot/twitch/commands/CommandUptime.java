package fr.funixgaming.funixbot.twitch.commands;

import com.funixproductions.api.client.twitch.reference.dtos.responses.channel.stream.TwitchStreamDTO;
import com.funixproductions.core.tools.time.TimeUtils;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.commands.utils.entities.BotCommand;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandUptime extends BotCommand {

    private final FunixBot bot;

    public CommandUptime(final FunixBot funixBot) {
        super("uptime");
        this.bot = funixBot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NonNull String[] args) {
        final String channel = user.getChannelName();
        final TwitchStreamDTO streamInfo = bot.getTwitchStatus().getFunixStreamInfo();

        if (streamInfo != null) {
            bot.sendChatMessage(channel, String.format("%s Le stream a commencé depuis %s.", TwitchEmotes.TWITCH_LOGO, getUptime(streamInfo)));
        } else {
            bot.sendChatMessage(channel, TwitchEmotes.TWITCH_LOGO + " Le stream est hors ligne. !discord & !twitter pour savoir quand je stream.");
        }
    }

    @NonNull
    public String getUptime(@NonNull final TwitchStreamDTO stream) {
        final Instant now = Instant.now();
        final Instant streamStart = stream.getStartedAt().toInstant();
        final List<String> uptimeStream = new ArrayList<>();

        long durationSeconds = TimeUtils.diffBetweenInstants(streamStart, now).getSeconds();

        if (durationSeconds >= 86400) {
            final long days = durationSeconds / 86400;
            final long secondsToRemove = 86400 * days;

            uptimeStream.add(String.format("%d %s", days, days == 1 ? "jour" : "jours"));
            durationSeconds -= secondsToRemove;
        }
        if (durationSeconds >= 3600) {
            final long hours = durationSeconds / 3600;
            final long secondsToRemove = 3600 * hours;

            uptimeStream.add(String.format("%d %s", hours, hours == 1 ? "heure" : "heures"));
            durationSeconds -= secondsToRemove;
        }
        if (durationSeconds >= 60) {
            final long minutes = durationSeconds / 60;
            final long secondsToRemove = 60 * minutes;

            uptimeStream.add(String.format("%d %s", minutes, minutes == 1 ? "minute" : "minutes"));
            durationSeconds -= secondsToRemove;
        }
        if (durationSeconds >= 1) {
            uptimeStream.add(String.format("%d secondes", durationSeconds));
        }
        return String.join(" ", uptimeStream);
    }

}
