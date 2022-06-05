package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommand extends BotCommand {
    private final AtomicBoolean atomicBoolean;

    public TestCommand(String commandName, AtomicBoolean atomicBoolean, String... aliases) {
        super(commandName, aliases);
        this.atomicBoolean = atomicBoolean;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NotNull String[] args) {
        assertEquals(3, args.length);
        assertEquals("salut", args[0]);
        assertEquals("les", args[1]);
        assertEquals("potes", args[2]);
        atomicBoolean.set(true);
    }
}
