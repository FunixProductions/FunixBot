package fr.gamecreep.bot.commands.utils;

import fr.gamecreep.bot.commands.CommandGnou;
import fr.gamecreep.bot.commands.CommandHelp;
import fr.gamecreep.bot.commands.CommandIP;
import fr.gamecreep.bot.commands.CommandMe;
import fr.gamecreep.bot.commands.utils.BotCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
    private List<BotCommand> commandList;
    private CommandIP ipcmd = new CommandIP();
    private CommandMe mecmd = new CommandMe();
    private CommandGnou gnoucmd = new CommandGnou();
    private CommandHelp helpcmd = new CommandHelp();

    public CommandList() {
        commandList = new ArrayList<BotCommand>();

        commandList.add(ipcmd);
        commandList.add(mecmd);
        commandList.add(gnoucmd);
        commandList.add(helpcmd);

    }

    public List getList() {
        return commandList;
    }

}