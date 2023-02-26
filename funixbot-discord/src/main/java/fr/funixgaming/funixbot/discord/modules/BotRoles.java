package fr.funixgaming.funixbot.discord.modules;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Getter
public class BotRoles {

    private final Role twitchNotifRole;
    private final Role youtubeNotifRole;
    private final Role tiktokNotifRole;
    private final Role followerRole;

    public BotRoles(final FunixBot funixBot) throws FunixBotException {
        final Guild guild = funixBot.getBotGuild();
        final BotConfig botConfig = funixBot.getBotConfig();

        this.twitchNotifRole = guild.getRoleById(botConfig.getTwitchNotifRoleId());
        this.youtubeNotifRole = guild.getRoleById(botConfig.getYoutubeNotifRoleId());
        this.tiktokNotifRole = guild.getRoleById(botConfig.getTiktokNotifRoleId());
        this.followerRole = guild.getRoleById(botConfig.getFollowerRoleId());

        if (this.twitchNotifRole == null || this.youtubeNotifRole == null || this.tiktokNotifRole == null || this.followerRole == null) {
            throw new FunixBotException("L'un des rôles n'existe pas sur le discord.");
        }
    }

}
