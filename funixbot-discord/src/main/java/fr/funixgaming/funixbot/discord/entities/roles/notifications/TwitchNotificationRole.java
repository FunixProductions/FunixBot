package fr.funixgaming.funixbot.discord.entities.roles.notifications;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import lombok.Getter;

@Getter
public class TwitchNotificationRole extends FunixBotRole {
    public static final String NAME = "twitch-notification";

    private final String roleUniqueName = NAME;

    public TwitchNotificationRole(final FunixBot funixBot) throws FunixBotException {
        super(funixBot.getBotGuild(),
                funixBot.getBotConfig().getTwitchNotifRoleId()
        );
    }
}
