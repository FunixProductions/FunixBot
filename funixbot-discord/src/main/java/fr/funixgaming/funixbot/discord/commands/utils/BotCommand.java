package fr.funixgaming.funixbot.commands.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BotCommand implements UserCommandEvent {
    private final String name;
    private final String description;

    public BotCommand(final String name, final String description) {
        this.name = name.toLowerCase();
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
