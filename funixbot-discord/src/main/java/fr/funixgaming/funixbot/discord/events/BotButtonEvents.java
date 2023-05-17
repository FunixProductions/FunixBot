package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to handle the buttons click
 */
@RequiredArgsConstructor
@Slf4j(topic = "ButtonsEvents")
public class BotButtonEvents extends ListenerAdapter {

    private final FunixBot funixBot;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        final String componentId = event.getInteraction().getComponentId();
        final FunixBotRole role = funixBot.getRoleByName(componentId);

        try {
            if (role != null) {
                handleRole(role.getRole(), event);
            }
        } catch (FunixBotException e) {
            log.error("Error while interract with button add role {}.", role.getRoleUniqueName(), e);
        }
    }

    /**
     * Check for every member roles if he has it.
     * If yes, remove it
     * If not, add it
     *
     * @param roleToAdd discord role
     * @param event button event click
     */
    private void handleRole(@NonNull Role roleToAdd, @NonNull ButtonInteractionEvent event) throws FunixBotException {
        final Member member = event.getMember();
        if (member == null) {
            throw new FunixBotException("Event click role, member not found.");
        }

        for (final Role userRole : member.getRoles()) {
            if (userRole.equals(roleToAdd)) {
                removeRole(userRole, event);
                return;
            }
        }

        addRole(roleToAdd, event);
    }

    private void addRole(@NonNull Role role, @NonNull ButtonInteractionEvent event) throws FunixBotException {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();

        if (member == null) {
            throw new FunixBotException("Event add role, member not found.");
        }
        if (guild == null) {
            throw new FunixBotException("Event add role, guild not found");
        }

        guild.addRoleToMember(member, role).queue();
        event.reply(String.format("Le rôle %s t'as été ajouté avec succès !", role.getName())).setEphemeral(true).queue();
        log.info("User {} add role {}", member.getEffectiveName(), role.getName());
    }

    private void removeRole(@NonNull Role role, @NonNull ButtonInteractionEvent event) throws FunixBotException {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();

        if (member == null) {
            throw new FunixBotException("Event add role, member not found.");
        }
        if (guild == null) {
            throw new FunixBotException("Event add role, guild not found");
        }

        guild.removeRoleFromMember(member, role).queue();
        event.reply(String.format("Le rôle %s t'as été supprimé avec succès !", role.getName())).setEphemeral(true).queue();
        log.info("User {} removed role {}", member.getEffectiveName(), role.getName());
    }

}
