package fr.funixgaming.funixbot.discord.configs;

import fr.funixgaming.funixbot.discord.FunixBot;
import fr.funixgaming.funixbot.discord.events.BotButtonEvents;
import fr.funixgaming.funixbot.discord.events.BotGuildEvents;
import fr.funixgaming.funixbot.discord.events.BotMessagesEvents;
import fr.funixgaming.funixbot.discord.events.BotSlashCommandsEvents;
import fr.funixgaming.funixbot.discord.modules.RoleMessageHandler;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdaEventsConfig {

    @Bean
    public RoleMessageHandler roleMessageHandler(BotConfigGenerated botConfigGenerated,
                                                 FunixBot funixBot,
                                                 JDA jda) {
        return new RoleMessageHandler(botConfigGenerated, funixBot, jda);
    }

    @Bean
    public BotMessagesEvents botMessagesEvents(RoleMessageHandler roleMessageHandler,
                                               JDA jda) {
        final BotMessagesEvents messagesEvents = new BotMessagesEvents(roleMessageHandler);

        jda.addEventListener(messagesEvents);
        return messagesEvents;
    }

    @Bean
    public BotGuildEvents botGuildEvents(FunixBot funixBot,
                                         BotConfig botConfig,
                                         JDA jda) {
        final BotGuildEvents botGuildEvents = new BotGuildEvents(botConfig, funixBot);

        jda.addEventListener(botGuildEvents);
        return botGuildEvents;
    }

    @Bean
    public BotSlashCommandsEvents botSlashCommandsEvents(FunixBot funixBot,
                                                         JDA jda) {
        final BotSlashCommandsEvents botSlashCommandsEvents = new BotSlashCommandsEvents(funixBot);

        jda.addEventListener(botSlashCommandsEvents);
        return botSlashCommandsEvents;
    }
    
    @Bean
    public BotButtonEvents botButtonEvents(FunixBot funixBot,
                                                         JDA jda) {
        final BotButtonEvents botButtonEvents = new BotButtonEvents(funixBot);

        jda.addEventListener(botButtonEvents);
        return botButtonEvents;
    }

}
