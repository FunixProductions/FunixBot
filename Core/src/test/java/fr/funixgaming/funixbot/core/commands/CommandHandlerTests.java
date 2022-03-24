package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.commands.entities.UserCommandEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandHandlerTests {

    @Test
    public void testChatCommand() {
        final String commandTest = "test";
        final String message = "!test salut les potes";
        AtomicBoolean eventTriggered = new AtomicBoolean(false);

        final UserCommandEvent commandEvent = (user, command, args) -> {
            assertEquals(commandTest, command);
            assertEquals(3, args.length);
            assertEquals("salut", args[0]);
            assertEquals("les", args[1]);
            assertEquals("potes", args[2]);
            eventTriggered.set(true);
        };

        final CommandHandler commandHandler = CommandHandler.getInstance();
        commandHandler.addListener(commandTest, commandEvent);
        commandHandler.onNewChat(null, message);

        final Instant limit = Instant.now().plusSeconds(10);
        while (!eventTriggered.get() && Instant.now().isBefore(limit));
        assertTrue(eventTriggered.get());
    }

}
