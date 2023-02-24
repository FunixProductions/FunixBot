package fr.gamecreep.bot.commands;

import fr.gamecreep.bot.commands.CommandCategories;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Commands {

    gnou(
            "gnou",
            "Incroyable blague 👀",
            CommandCategories.BLAGUE.name(),
            "Alors c'est l'histoire d'un gnou qui se balade dans la savane et qui croise un autre groupe de gnou." + "\nL'autre groupe de gnou le voyant tout seul lui a donc demandé : " + "\nEh viens avec gnou :water_buffalo:",
            true
    );

    private String name;
    private String message;
    private String description;
    private String category;
    private boolean ephemeral;

    Commands(String name, String description, String category, String message, boolean ephemeral) {
        this.name = name;
        this.message = message;
        this.description = description;
        this.category = category;
        this.ephemeral = ephemeral;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() { return category; }
    public String getMessage() {
        return message;
    }

    public boolean getEphemeral() {
        return ephemeral;
    }

    public void run(SlashCommandInteractionEvent interaction) {
        interaction.reply(message).setEphemeral(ephemeral).queue();
    }

    public void run(MessageReceivedEvent event) {
        event.getChannel().sendMessage(message).queue();
    }

}
