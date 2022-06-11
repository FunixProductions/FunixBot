package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.configs.BotConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BotGuildEvents extends ListenerAdapter {

    private final BotConfig botConfig;

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
    }

    private void guildMemberEventLog() {

    }
}
