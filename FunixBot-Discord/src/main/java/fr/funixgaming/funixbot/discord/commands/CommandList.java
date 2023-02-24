package fr.gamecreep.bot.commands;

import fr.gamecreep.bot.commands.*;

public enum CommandList {

    help("help", EmbedCommands.help.getDescription(), CommandCategories.INFORMATIONS.name()),
    ping("ping", EmbedCommands.ping.getDescription(),CommandCategories.INFORMATIONS.name()),
    me("me", EmbedCommands.me.getDescription(), CommandCategories.INFORMATIONS.name()),
    ip("ip", EmbedCommands.ip.getDescription(), CommandCategories.PACIFISTA.name()),
    gnou("gnou", Commands.gnou.getDescription(), CommandCategories.BLAGUE.name());

    private String name;
    private String description;
    private String category;

    CommandList(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
