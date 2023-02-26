package fr.funixgaming.funixbot.twitch.commands.utils.entities;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class BotCommand implements UserCommandEvent {
    private static final int COOLDOWN = 3;

    private final String commandName;
    private final String[] aliases;

    private Instant lastCommandUsageTime = Instant.now();

    public BotCommand(final String commandName, final String... aliases) {
        this.commandName = commandName.toLowerCase();
        this.aliases = new String[aliases.length];

        for (int i = 0; i < aliases.length; ++i) {
            this.aliases[i] = aliases[i].toLowerCase();
        }
    }

    public boolean canUseCommand() {
        final Instant now = Instant.now();

        if (now.isAfter(lastCommandUsageTime.plusSeconds(COOLDOWN))) {
            lastCommandUsageTime = now;
            return true;
        } else {
            return false;
        }
    }
}
