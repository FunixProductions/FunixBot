package fr.funixgaming.funixbot.discord.entities.roles.utils;

import com.google.common.base.Strings;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Getter
public abstract class FunixBotRole implements DiscordRole {

    private final String roleId;
    private final Role role;

    protected FunixBotRole(final Guild botGuild,
                           final String roleId) throws FunixBotException {
        if (botGuild == null) {
            throw new FunixBotException("La guilde du bot est nulle.");
        }
        if (Strings.isNullOrEmpty(roleId)) {
            throw new FunixBotException(String.format("Le role id pour la création du rôle " +
                    "%s est vide.", this.getRoleUniqueName()));
        }

        this.roleId = roleId;
        this.role = botGuild.getRoleById(this.getRoleId());

        if (this.role == null) {
            throw new FunixBotException(String.format("Le role %s n'existe pas sur le discord." +
                    "Veuillez le créer et le définir en role id.", this.getRoleUniqueName()));
        }
    }


}
