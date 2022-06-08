package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.core.modules.TwitchStreamStatus;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.reference.entities.responses.channel.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MultiTwitchCommand extends BotCommand {

    private static final String MULTISTREAM_URL = "multistre.am/";

    private final FunixBot bot;

    public MultiTwitchCommand(final FunixBot funixBot) {
        super("multitwitch", "multistream", "squad");
        this.bot = funixBot;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull @NonNull String[] args) {
        try {
            final Stream stream = TwitchStreamStatus.getInstance().getStream();

            if (stream != null) {
                final Set<String> streamersTitle = getStreamersInTitle(stream.getTitle());

                if (!streamersTitle.isEmpty()) {
                    bot.sendChatMessage(user.getChannelName(), String.format(
                            "%s MultiTwitch -> %s/%s",
                            TwitchEmotes.TWITCH_LOGO,
                            MULTISTREAM_URL + bot.getTwitchConfig().getStreamerUsername(),
                            String.join("/", streamersTitle)
                    ));
                }
            }
        } catch (FunixBotException e) {
            log.error("Erreur lors de l'execution de la commande multitwitch. {}", e.getMessage());
        }
    }

    /**
     * Get Streamers in title with @ before username
     * @param title twitch stream title
     * @return Set streamers names
     */
    private Set<String> getStreamersInTitle(final String title) {
        final Set<String> streamers = new HashSet<>();

        for (final String word : title.split(" ")) {
            if (word.startsWith("@") && word.length() > 3) {
                final String streamerName = getStreamerNameInTag(word);

                if (!streamerName.isBlank()) {
                    streamers.add(streamerName);
                }
            }
        }
        return streamers;
    }

    private String getStreamerNameInTag(final String tag) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final char c : tag.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
}
