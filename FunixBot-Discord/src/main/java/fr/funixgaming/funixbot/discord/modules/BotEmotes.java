package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import fr.funixgaming.funixbot.discord.FunixBot;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Getter
public class BotEmotes {

    private final FunixBot funixBot;

    private final Emote twitchEmote;
    private final Emote youtubeEmote;
    private final Emote tiktokEmote;

    public BotEmotes(final FunixBot funixBot) throws FunixBotException {
        this.funixBot = funixBot;
        final Guild guild = funixBot.getJda().getGuildById(funixBot.getBotConfig().getGuildId());

        if (guild != null) {
            this.twitchEmote = searchEmote("twitch", guild);
            this.youtubeEmote = searchEmote("youtube", guild);
            this.tiktokEmote = searchEmote("tiktok", guild);
        } else {
            throw new FunixBotException("La guilde spécifiée dans la config n'existe pas.");
        }
    }

    private static Emote searchEmote(@NonNull final String emoteName, @NonNull final Guild guild) throws FunixBotException {
        final List<Emote> emoteList = guild.getEmotesByName(emoteName, false);

        for (final Emote emote : emoteList) {
            if (emote.getName().equals(emoteName)) {
                return emote;
            }
        }
        return createEmoteFromClassPath(emoteName, guild);
    }

    private static Emote createEmoteFromClassPath(@NonNull final String emoteName, @NonNull final Guild guild) throws FunixBotException {
        try {
            final BufferedImage bufferedImage = DataFiles.getImageFromClasspath(String.format("/emotes/%s.png", emoteName));
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            final Icon icon = Icon.from(byteArrayOutputStream.toByteArray(), Icon.IconType.PNG);

            return guild.createEmote(emoteName, icon).complete();
        } catch (IOException e) {
            throw new FunixBotException(e.getMessage(), e);
        }
    }

}
