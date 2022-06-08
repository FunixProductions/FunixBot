package fr.funixgaming.funixbot.discord.commands;

public interface SlashCommand {
    String getName();
    String getDescription();
    void runCommand();
}
