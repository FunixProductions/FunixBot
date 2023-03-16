package fr.funixgaming.funixbot.discord.entities.roles.users;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import lombok.Getter;

@Getter
public class FollowrRole extends FunixBotRole {
    public static final String NAME = "follower";

    private final String roleUniqueName = NAME;

    public FollowrRole(final FunixBot funixBot) throws FunixBotException {
        super(funixBot.getBotGuild(),
                funixBot.getBotConfig().getFollowerRoleId()
        );
    }

}
