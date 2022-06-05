package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.BotTest;
import fr.funixgaming.funixbot.core.TestApp;
import fr.funixgaming.funixbot.core.commands.entities.SimpleCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.chatbot_irc.parsers.TagParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {
                TestApp.class
        }
)
public class CommandHandlerTests {

    private final CommandHandler commandHandler;
    private final AtomicBoolean eventTriggered = new AtomicBoolean(false);
    private final ChatMember chatMember = new ChatMember(new TagParser("@badge-info=;badges=broadcaster/1;client-nonce=28e05b1c83f1e916ca1710c44b014515;color=#0000FF;display-name=foofoo;emotes=62835:0-10;first-msg=0;flags=;id=f80a19d6-e35a-4273-82d0-cd87f614e767;mod=0;room-id=713936733;subscriber=0;tmi-sent-ts=1642696567751;turbo=0;user-id=713936733;user-type= :foofoo!foofoo@foofoo.tmi.twitch.tv PRIVMSG #bar :bleedPurple"));

    @Autowired
    public CommandHandlerTests(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;

        TestCommand botCommand = new TestCommand("test", eventTriggered, "t");
        commandHandler.addListener(botCommand);
    }

    @Test
    public void testChatCommand() {
        final String message = "!test salut les potes";
        commandHandler.onNewChat(chatMember, message, new BotTest(), "test");

        final Instant start = Instant.now().plusSeconds(20);
        while (!eventTriggered.get() && Instant.now().isBefore(start));

        assertTrue(eventTriggered.get());
    }

    @Test
    public void testChatCommandAlias() {
        final String message = "!t salut les potes";
        commandHandler.onNewChat(chatMember, message, new BotTest(), "test");

        final Instant start = Instant.now().plusSeconds(20);
        while (!eventTriggered.get() && Instant.now().isBefore(start));

        assertTrue(eventTriggered.get());
    }

    @Test
    public void testGetCommandsFromClassPath() throws Exception {
        final Set<SimpleCommand> commands = SimpleCommand.getCommandsFromClasspath();
        int checked = 0;

        assertEquals(3, commands.size());
        for (final SimpleCommand command : commands) {
            if (command.getCommandName().equals("site")) {
                assertEquals("site", command.getCommandName());
                assertEquals("Site web : funixgaming.fr", command.getResponse());
                ++checked;
            } else if (command.getCommandName().equals("pacifista")) {
                assertEquals("pacifista", command.getCommandName());
                assertEquals("Minecraft server", command.getResponse());
                assertEquals(2, command.getAliases().length);
                assertEquals("ip", command.getAliases()[0]);
                assertEquals("serveur", command.getAliases()[1]);
                ++checked;
            } else if (command.getCommandName().equals("discord")) {
                assertEquals("discord", command.getCommandName());
                assertEquals("Discord de la commu : discord.funixgaming.fr", command.getResponse());
                ++checked;
            }
        }
        assertEquals(3, checked);
    }

}
