package fr.funixgaming.funixbot.discord.commands;

public enum CommandCategories {

    INFORMATIONS("Informations"),
    PACIFISTA("Pacifista"),
    BLAGUE("Ptite blague");

    private String category;

    CommandCategories(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
