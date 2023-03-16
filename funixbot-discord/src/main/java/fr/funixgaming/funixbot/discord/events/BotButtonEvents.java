package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.modules.BotRoles;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.List;

public class BotButtonEvents extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);

        FunixBot funixBot = null;
        final BotRoles botRoles = funixBot.getBotRoles();

        Role twitchrole = botRoles.getTwitchNotifRole();
        Role youtuberole = botRoles.getYoutubeNotifRole();
        Role tiktokrole = botRoles.getTiktokNotifRole();

        switch (event.getInteraction().getComponentId()) {
            case "notif-twitch":
                handleRole(twitchrole, event);
                break;
            case "notif-youtube":
                handleRole(youtuberole, event);
                break;
            case "notif-tiktok":
                handleRole(tiktokrole, event);
                break;
        }

    }

    public void handleRole(@NonNull Role role, @NonNull ButtonInteractionEvent event) {
        Member member = event.getMember();
        List<Role> roles = member.getRoles();
        /*
            Check for every member roles if he has it.
            If yes, remove it
            If not, add it
        */
        int i;
        for (i = 0; i < roles.size(); i++) {
            Role r = roles.get(i);
            if (r.equals(role)) {
                removeRole(role, event);
                i = 0;
                return;
            }
        }

        if (i == roles.size()) {
            addRole(role, event);
        }
    }

    public void addRole(@NonNull Role role, @NonNull ButtonInteractionEvent event) {
        Member member = event.getMember();
        event.getGuild().addRoleToMember(member, role).queue();
        event.reply(String.format("Le rôle <@&%s> t'as été ajouté avec succès !", role.getId())).setEphemeral(true).queue();
    }

    public void removeRole(@NonNull Role role, @NonNull ButtonInteractionEvent event) {
        Member member = event.getMember();
        event.getGuild().removeRoleFromMember(member, role).queue();
        event.reply(String.format("Le rôle <@&%s> t'as été supprimé avec succès !", role.getId())).setEphemeral(true).queue();
    }

}