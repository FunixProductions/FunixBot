package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.commands.entities.SimpleCommand;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandHandlerTests {

    private final CommandHandler commandHandler = CommandHandler.getInstance();
    private final AtomicBoolean eventTriggered = new AtomicBoolean(false);

    public CommandHandlerTests() {
        final TestCommand botCommand = new TestCommand("test", eventTriggered, "t");
        commandHandler.addListener(botCommand);
    }

    @Test
    public void testChatCommand() throws InterruptedException {
        final String message = "!test salut les potes";
        commandHandler.onNewChat(null, message);

        Thread.sleep(1000);
        assertTrue(eventTriggered.get());
    }

    @Test
    public void testChatCommandAlias() throws InterruptedException {
        final String message = "!t salut les potes";
        commandHandler.onNewChat(null, message);

        Thread.sleep(1000);
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
