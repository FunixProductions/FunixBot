package fr.funixgaming.funixbot.discord.commands.utils;

import fr.funixgaming.funixbot.discord.commands.*;
import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class CommandList {
    @Getter private List<SlashCommand> commandList;
    private CommandIP ipcmd = new CommandIP();
    private CommandMe mecmd = new CommandMe();
    private CommandGnou gnoucmd = new CommandGnou();
    private CommandHelp helpcmd = new CommandHelp();

    public CommandList() {
        commandList = new ArrayList<SlashCommand>();

        commandList.add(ipcmd);
        commandList.add(mecmd);
        commandList.add(gnoucmd);
        commandList.add(helpcmd);

    }
}
