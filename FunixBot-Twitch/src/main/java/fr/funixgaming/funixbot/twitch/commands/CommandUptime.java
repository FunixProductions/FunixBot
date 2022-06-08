package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.api.core.utils.time.TimeUtils;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.core.modules.TwitchStreamStatus;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.reference.entities.responses.channel.Stream;
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
        try {
            final String channel = user.getChannelName();
            final TwitchStreamStatus streamStatus = TwitchStreamStatus.getInstance();
            final Stream stream = streamStatus.getStream();

            if (stream != null) {
                bot.sendChatMessage(channel, String.format("%s Le stream a commencé depuis %s.", TwitchEmotes.TWITCH_LOGO, getUptime(stream)));
            } else {
                bot.sendChatMessage(channel, TwitchEmotes.TWITCH_LOGO + " Le stream est hors ligne. !discord & !twitter pour savoir quand je stream.");
            }
        } catch (FunixBotException e) {
            log.error("Erreur command uptime: {}", e.getMessage());
        }
    }

    @NonNull
    public String getUptime(@NonNull final Stream stream) {
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
