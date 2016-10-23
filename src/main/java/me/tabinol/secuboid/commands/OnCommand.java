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
package me.tabinol.secuboid.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandHelp;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


/**
 * The Class OnCommand.
 */
public class OnCommand extends Thread implements CommandExecutor {

    private final Map<String, Class<?>> commands;
    
    /**
     * Instantiates a new on command.
     */
    public OnCommand() {
        
        // Create Command list
        commands = new TreeMap<String, Class<?>>();
        
        for(CommandClassList presentClass : CommandClassList.values()) {
            // Store commands information
            InfoCommand infoCommand = presentClass.getCommandClass().getAnnotation(InfoCommand.class);
            commands.put(infoCommand.name().toLowerCase(), presentClass.getCommandClass());
            for(String alias : infoCommand.aliases()) {
                commands.put(alias.toLowerCase(), presentClass.getCommandClass());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */

    /**
     *
     * @param sender
     * @param cmd
     * @param label
     * @param arg
     * @return
     */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        // Others commands then /secuboid, /claim and /fd will not be send.
        
        ArgList argList = new ArgList(arg, sender);
            try {
                // Check the command to send
                getCommand(sender, cmd, argList);
                return true;
                // If error on command, send the message to the player
            } catch (SecuboidCommandException ex) {
                return true;
            }
    }

    // Get command from args
    private void getCommand(CommandSender sender, Command cmd, ArgList argList) throws SecuboidCommandException {

        try {
            // Show help if there is no arguments
            if (argList.isLast()) {
                new CommandHelp(this, sender, "GENERAL").commandExecute();
                return;
            }

            String command = argList.getNext().toLowerCase();

            // take the name
            Class<?> cv = commands.get(command.toLowerCase());
            
            // The command does not exist
            if(cv == null) {
                throw new SecuboidCommandException("Command not existing", sender, "COMMAND.NOTEXIST", "SECUBOID");
            }
            
            // Remove page from memory if needed
            if(cv != commands.get("page")) {
                Secuboid.getThisPlugin().getPlayerConf().get(sender).setChatPage(null);
            }

            // Do the command
            InfoCommand ci = cv.getAnnotation(InfoCommand.class);
            CommandExec ce = (CommandExec) cv.getConstructor(CommandEntities.class)
                    .newInstance(new CommandEntities(ci, sender, argList, this));
            if (ce.isExecutable()) {
                ce.commandExecute();
            }

            // a huge number of Exception to catch!
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (SecurityException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InstantiationException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InvocationTargetException ex) {
            // Noting to do, it will trows SecuboidCommandException
        }
    }
    
    /**
     *
     * @param command
     * @return
     */
    public InfoCommand getInfoCommand(String command) {
        
        Class<?> infoClass = commands.get(command.toLowerCase());
        
        if(infoClass == null) {
            return null;
        }
        
        return infoClass.getAnnotation(InfoCommand.class);
    }
}
