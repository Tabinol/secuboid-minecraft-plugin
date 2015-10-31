/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.commands.executor;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.OnCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;

import org.bukkit.command.CommandSender;


/**
 * The Class CommandHelp.
 */
@InfoCommand(name="help", allowConsole=true)
public class CommandHelp extends CommandExec {

    /** The sender. */
    private final CommandSender sender;
    
    private final OnCommand onCommand;
    
    /** The command name. */
    private String commandName = null;
    

    /**
     * Instantiates a new command help.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandHelp(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
        sender = entity.sender;
        onCommand = entity.onCommand;
    }

    // Call directly the Help without verification CommandName is UPERCASE
    /**
     * Instantiates a new command help.
     *
     * @param onCommand the on command
     * @param sender the sender
     * @param commandName the command name
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandHelp(OnCommand onCommand, CommandSender sender, 
            String commandName) throws SecuboidCommandException {

        super(null);
        this.sender = sender;
        this.commandName = commandName.toUpperCase();
        this.onCommand = onCommand;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        if (commandName == null) {
            String arg = entity.argList.getNext();

            if (arg == null) {
                commandName = "GENERAL";
            } else {
                // Will throw an exception if the command name is invalid
                try {
                    InfoCommand infoCommand = onCommand.getInfoCommand(arg);
                    if(infoCommand != null) {
                        commandName = infoCommand.name().toUpperCase();
                    } else {
                        // Invalid command, just arg and will run Exception with showHelp()
                        commandName = arg;
                    }
                } catch (IllegalArgumentException ex) {
                    commandName = "GENERAL";
                }
            }
        }
        
        showHelp();
    }

    /**
     * Show help.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void showHelp() throws SecuboidCommandException {

        String help = Secuboid.getThisPlugin().getLanguage().getHelp("SECUBOID", commandName);
        
        // If there is no help for this command
        if(help == null) {
            throw new SecuboidCommandException("Command with no help", sender, "HELP.NOHELP");
        }
        
        if (commandName.equals("GENERAL")) {
            new ChatPage("HELP.LISTSTART", help, sender, null).getPage(1);
        } else {
            sender.sendMessage(help);
        }
    }

}
