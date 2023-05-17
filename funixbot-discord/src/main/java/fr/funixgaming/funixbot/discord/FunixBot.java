package fr.funixgaming.funixbot.discord;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.entities.commands.CommandGnou;
import fr.funixgaming.funixbot.discord.entities.commands.CommandIP;
import fr.funixgaming.funixbot.discord.entities.commands.CommandMe;
import fr.funixgaming.funixbot.discord.entities.commands.utils.DiscordCommand;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.TiktokNotificationRole;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.TwitchNotificationRole;
import fr.funixgaming.funixbot.discord.entities.roles.notifications.YoutubeNotificationRole;
import fr.funixgaming.funixbot.discord.entities.roles.users.FollowrRole;
import fr.funixgaming.funixbot.discord.entities.roles.utils.FunixBotRole;
import fr.funixgaming.funixbot.discord.modules.BotEmotes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j(topic = "FunixBot")
@Getter
@Service
public class FunixBot {
    private final JDA jda;
    private final BotConfig botConfig;
    private final BotEmotes botEmotes;
    private final Guild botGuild;

    private final Set<DiscordCommand> botCommands = new HashSet<>();
    private final Set<FunixBotRole> botRoles = new HashSet<>();

    public FunixBot(BotConfig botConfig, JDA jda) throws Exception {
        try {
            this.jda = jda;
            this.botConfig = botConfig;

            this.botGuild = jda.getGuildById(botConfig.getGuildId());
            if (this.botGuild == null) {
                throw new FunixBotException("La guilde id du bot est invalide.");
            }

            this.botEmotes = new BotEmotes(this);

            setupCommands();
            setupRoles();

            log.info("Discord bot prêt ! Lien d'invitation : {}", this.jda.getInviteUrl(Permission.ADMINISTRATOR));
        } catch (Exception e) {
            log.error("Une erreur est survenue lors du lancement du bot discord. {}", e.getMessage());
            throw e;
        }
    }

    private void setupCommands() {
        this.botCommands.add(new CommandGnou(this.jda));
        this.botCommands.add(new CommandIP(this.jda));
        this.botCommands.add(new CommandMe(this.jda));
    }

    private void setupRoles() throws FunixBotException {
        this.botRoles.add(new FollowrRole(this));
        this.botRoles.add(new TiktokNotificationRole(this));
        this.botRoles.add(new TwitchNotificationRole(this));
        this.botRoles.add(new YoutubeNotificationRole(this));
    }

    @Nullable
    public FunixBotRole getRoleByName(final String name) {
        for (final FunixBotRole funixBotRole : this.botRoles) {
            if (funixBotRole.getRoleUniqueName().equals(name)) {
                return funixBotRole;
            }
        }
        return null;
    }

    public void sendChatMessage(String channelId, String message) {
        final MessageChannel channel = this.jda.getChannelById(MessageChannel.class, channelId);

        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public void sendChatMessage(String channelId, MessageEmbed messageEmbed) {
        final MessageChannel channel = this.jda.getChannelById(MessageChannel.class, channelId);

        if (channel != null) {
            channel.sendMessageEmbeds(messageEmbed).queue();
        }
    }
}
