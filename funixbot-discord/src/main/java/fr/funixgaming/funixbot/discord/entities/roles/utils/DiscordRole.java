package fr.funixgaming.funixbot.discord.entities.roles.utils;

import lombok.NonNull;

public interface DiscordRole {
    @NonNull String getRoleId();
    @NonNull String getRoleUniqueName();
}
