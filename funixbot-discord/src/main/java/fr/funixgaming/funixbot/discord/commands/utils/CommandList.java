package fr.funixgaming.funixbot.discord.commands.utils;

import fr.funixgaming.funixbot.discord.commands.*;
import fr.funixgaming.funixbot.discord.commands.utils.SlashCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
    private List<SlashCommand> commandList;
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

    public List getList() {
        return commandList;
    }

}
