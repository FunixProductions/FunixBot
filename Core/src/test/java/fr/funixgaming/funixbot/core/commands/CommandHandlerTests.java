package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.commands.entities.SimpleCommand;
import org.junit.jupiter.api.Test;

import java.util.Map;
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
        final Map<String, String> commands = SimpleCommand.getCommandsFromClasspath();
        boolean firstCommand = true;

        assertEquals(2, commands.size());
        for (final Map.Entry<String, String> entry : commands.entrySet()) {
            if (firstCommand) {
                assertEquals("site", entry.getKey());
                assertEquals("Site web : funixgaming.fr", entry.getValue());
                firstCommand = false;
            } else {
                assertEquals("discord", entry.getKey());
                assertEquals("Discord de la commu : discord.funixgaming.fr", entry.getValue());
            }
        }
    }

}
