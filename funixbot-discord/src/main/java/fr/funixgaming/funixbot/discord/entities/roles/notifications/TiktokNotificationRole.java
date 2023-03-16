package fr.funixgaming.funixbot.discord.entities.roles.notifications;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import lombok.Getter;

@Getter
public class TiktokNotificationRole extends FunixBotRole {

    public static final String NAME = "tiktok-notification";

    private final String roleUniqueName = NAME;

    public TiktokNotificationRole(final FunixBot funixBot) throws FunixBotException {
        super(funixBot.getBotGuild(),
                funixBot.getBotConfig().getTiktokNotifRoleId()
        );
    }

}
