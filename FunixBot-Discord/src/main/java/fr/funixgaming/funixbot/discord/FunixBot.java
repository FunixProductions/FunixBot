package fr.funixgaming.funixbot.discord;

import fr.funixgaming.funixbot.core.Bot;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.modules.BotTwitchAuth;
import fr.funixgaming.funixbot.discord.configs.BotConfig;
import fr.funixgaming.funixbot.discord.events.BotGuildEvents;
import fr.funixgaming.funixbot.discord.events.BotMessagesEvents;
import fr.funixgaming.funixbot.discord.events.BotSlashCommandsEvents;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Service
public class FunixBot implements Bot, ServletContextListener {
    private static volatile FunixBot instance = null;

    private final JDA jda;
    private final BotConfig botConfig;
    private final BotTwitchAuth botTwitchAuth;

    private boolean running = true;
    private final Set<BotCommand> commands = new HashSet<>();

    public FunixBot(BotConfig botConfig,
                    BotTwitchAuth botTwitchAuth,
                    BotMessagesEvents botMessagesEvents,
                    BotSlashCommandsEvents botSlashCommandsEvents,
                    BotGuildEvents botGuildEvents) throws Exception {
        try {
            this.botConfig = botConfig;
            this.botTwitchAuth = botTwitchAuth;
            this.jda = buildBot(botMessagesEvents, botSlashCommandsEvents, botGuildEvents);
            log.info("Discord bot prêt ! Lien d'invitation : {}", this.jda.getInviteUrl(Permission.ADMINISTRATOR));

            instance = this;
        } catch (Exception e) {
            log.error("Une erreur est survenue lors du lancement du bot discord. {}", e.getMessage());
            throw e;
        }
    }

    private JDA buildBot(final BotMessagesEvents botMessagesEvents,
                         final BotSlashCommandsEvents botSlashCommandsEvents,
                         final BotGuildEvents botGuildEvents) throws LoginException, InterruptedException {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(botConfig.getBotToken());

        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.addEventListeners(botMessagesEvents, botSlashCommandsEvents, botGuildEvents);
        jdaBuilder.setActivity(Activity.of(Activity.ActivityType.WATCHING, "twitch.tv/funixgaming", "https://twitch.tv/funixgaming"));
        return jdaBuilder.build().awaitReady();
    }

    @Override
    public void sendChatMessage(String channel, String message) {

    }

    public void sendChatMessage(String channel, MessageEmbed messageEmbed) {

    }

    @Override
    public void addNewCommand(BotCommand command) {

    }

    @Override
    public void removeCommand(String commandName) {

    }

    @Override
    public void stopBot() {
        log.info("Arrêt du bot.");
        this.running = false;
        this.jda.shutdown();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.stopBot();
    }

    public static FunixBot getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("Le bot n'est pas chargé.");
        }
        return instance;
    }
}
