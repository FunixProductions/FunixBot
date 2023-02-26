package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.discord.FunixBot;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.springframework.lang.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Getter
public class BotEmotes {

    private final FunixBot funixBot;

    private final RichCustomEmoji twitchEmote;
    private final RichCustomEmoji youtubeEmote;
    private final RichCustomEmoji tiktokEmote;

    public BotEmotes(final FunixBot funixBot) throws FunixBotException {
        this.funixBot = funixBot;
        final Guild guild = funixBot.getBotGuild();

        this.twitchEmote = searchEmote("twitch", guild);
        this.youtubeEmote = searchEmote("youtube", guild);
        this.tiktokEmote = searchEmote("tiktok", guild);
    }

    private static RichCustomEmoji searchEmote(@NonNull final String emoteName, @NonNull final Guild guild) throws FunixBotException {
        final List<RichCustomEmoji> emoteList = guild.getEmojisByName(emoteName, false);

        for (final RichCustomEmoji emote : emoteList) {
            if (emote.getName().equals(emoteName)) {
                return emote;
            }
        }
        return createEmoteFromClassPath(emoteName, guild);
    }

    private static RichCustomEmoji createEmoteFromClassPath(@NonNull final String emoteName, @NonNull final Guild guild) throws FunixBotException {
        try {
            final BufferedImage bufferedImage = DataFiles.getImageFromClasspath(String.format("/emotes/%s.png", emoteName));
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            final Icon icon = Icon.from(byteArrayOutputStream.toByteArray(), Icon.IconType.PNG);

            return guild.createEmoji(emoteName, icon).complete();
        } catch (IOException e) {
            throw new FunixBotException(e.getMessage(), e);
        }
    }

}
