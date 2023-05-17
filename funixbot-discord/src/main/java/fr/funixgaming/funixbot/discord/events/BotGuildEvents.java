package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.entities.roles.users.FollowrRole;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import fr.funixgaming.funixbot.discord.enums.GuildEventType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.format.DateTimeFormatter;

@Slf4j
@AllArgsConstructor
public class BotGuildEvents extends ListenerAdapter {

    private final BotConfig botConfig;
    private final FunixBot funixBot;

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        guildMemberEventLog(event.getUser(), GuildEventType.BAN);
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        guildMemberEventLog(event.getUser(), GuildEventType.UNBAN);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        guildMemberEventLog(event.getUser(), GuildEventType.LEAVE);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        guildMemberEventLog(event.getUser(), GuildEventType.JOIN);

        final Guild guild = funixBot.getBotGuild();
        final FunixBotRole role = funixBot.getRoleByName(FollowrRole.NAME);
        if (role != null) {
            guild.addRoleToMember(event.getUser(), role.getRole()).queue();
        }
    }

    private void guildMemberEventLog(final User user, final GuildEventType eventType) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");

        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.addField("UserId", user.getId(), false);
        embedBuilder.addField("Date de création", dateTimeFormatter.format(user.getTimeCreated()), false);

        switch (eventType) {
            case JOIN -> {
                final EmbedBuilder generalEmbed = new EmbedBuilder();
                generalEmbed.setColor(Color.GREEN);
                generalEmbed.setDescription(String.format("%s a rejoint le discord.", user.getAsTag()));
                generalEmbed.setAuthor(user.getName(), null, user.getAvatarUrl());
                generalEmbed.setThumbnail(user.getAvatarUrl());

                embedBuilder.setColor(Color.GREEN);
                embedBuilder.setDescription(String.format("%s a rejoint le discord.", user.getAsTag()));

                funixBot.sendChatMessage(botConfig.getGeneralChannelId(), generalEmbed.build());
            }
            case LEAVE -> {
                embedBuilder.setColor(Color.RED);
                embedBuilder.setDescription(String.format("%s a quitté le discord.", user.getAsTag()));
            }
            case KICK -> {
                embedBuilder.setColor(Color.YELLOW);
                embedBuilder.setDescription(String.format("%s a été kick du discord.", user.getAsTag()));
            }
            case BAN -> {
                embedBuilder.setColor(Color.BLACK);
                embedBuilder.setDescription(String.format("%s a été ban du discord.", user.getAsTag()));
            }
            case UNBAN -> {
                embedBuilder.setColor(Color.GRAY);
                embedBuilder.setDescription(String.format("%s a été unban du discord.", user.getAsTag()));
            }
        }

        funixBot.sendChatMessage(botConfig.getLogChannelId(), embedBuilder.build());
    }
}
