package fr.funixgaming.funixbot.discord;

import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.modules.BotEmotes;
import fr.funixgaming.funixbot.discord.modules.BotRoles;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
public class FunixBot {
    private final JDA jda;
    private final BotConfig botConfig;
    private final BotEmotes botEmotes;
    private final BotRoles botRoles;
    private final Guild botGuild;

    public FunixBot(BotConfig botConfig, JDA jda) throws Exception {
        try {
            this.jda = jda;
            this.botConfig = botConfig;

            this.botGuild = jda.getGuildById(botConfig.getGuildId());
            if (this.botGuild == null) {
                throw new FunixBotException("La guilde id du bot est invalide.");
            }

            this.botEmotes = new BotEmotes(this);
            this.botRoles = new BotRoles(this);
            
            CommandList cmdList;
            cmdList = new CommandList();
            List<SlashCommand> commandList = cmdList.getList();

            for (SlashCommand cmd : commandList) {
                jda.upsertCommand(cmd.getName(), cmd.getDescription());
            }

            log.info("Discord bot prêt ! Lien d'invitation : {}", this.jda.getInviteUrl(Permission.ADMINISTRATOR));

        } catch (Exception e) {
            log.error("Une erreur est survenue lors du lancement du bot discord. {}", e.getMessage());
            throw e;
        }
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
