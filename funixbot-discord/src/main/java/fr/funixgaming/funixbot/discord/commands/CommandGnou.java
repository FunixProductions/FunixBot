package fr.funixgaming.funixbot.discord.commands;

import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandGnou implements SlashCommand {

    private String name = "gnou";
    private String description = "Blague drôle 👀";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        String reply = "Alors c'est l'histoire d'un gnou qui se balade dans la savane et qui croise un autre groupe de gnou." + "\nL'autre groupe de gnou le voyant tout seul lui a donc demandé : " + "\nEh viens avec gnou :water_buffalo:";

        interactionEvent.reply(reply).setEphemeral(true).queue();
    }
    
    @Override
    public void runCommand(@NonNull MessageReceivedEvent message) {
        String reply = "Alors c'est l'histoire d'un gnou qui se balade dans la savane et qui croise un autre groupe de gnou." + "\nL'autre groupe de gnou le voyant tout seul lui a donc demandé : " + "\nEh viens avec gnou :water_buffalo:";

        message.getChannel().sendMessage(reply).queue();
    }
}
