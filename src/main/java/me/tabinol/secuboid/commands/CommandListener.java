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
import me.tabinol.secuboid.commands.executor.CommandExec;
import me.tabinol.secuboid.commands.executor.CommandHelp;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The Class OnCommand.
 */
public class CommandListener implements CommandExecutor {

    private final Secuboid secuboid;
    private final Map<String, Class<?>> commands;

    /**
     * Instantiates a new on command.
     *
     * @param secuboid secuboid instance
     */
    public CommandListener(Secuboid secuboid) {

        this.secuboid = secuboid;

        // Create Command list
        commands = new TreeMap<String, Class<?>>();

        for (CommandClassList presentClass : CommandClassList.values()) {
            // Store commands information
            System.out.println(presentClass);
            InfoCommand infoCommand = presentClass.getCommandClass().getAnnotation(InfoCommand.class);
            commands.put(infoCommand.name().toLowerCase(), presentClass.getCommandClass());
            for (String alias : infoCommand.aliases()) {
                commands.put(alias.toLowerCase(), presentClass.getCommandClass());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        // Others commands then /secuboid, /claim and /fd will not be send.
        ArgList argList = new ArgList(secuboid, arg, sender);
        try {
            // Check the command to send
            getCommand(sender, cmd, argList);
            return true;
            // If error on command, send the message to the player
        } catch (SecuboidCommandException ex) {
            return true;
        }
    }

    /**
     * Gets command from args.
     *
     * @param sender  the sender
     * @param cmd     the command
     * @param argList arguments list
     * @throws SecuboidCommandException
     */
    private void getCommand(CommandSender sender, Command cmd, ArgList argList) throws SecuboidCommandException {

        try {
            // Show help if there is no arguments
            if (argList.isLast()) {
                new CommandHelp(secuboid, null, sender, null).commandExecute();
                return;
            }

            String command = argList.getNext().toLowerCase();

            // take the name
            Class<?> cv = commands.get(command.toLowerCase());

            // The command does not exist
            if (cv == null) {
                throw new SecuboidCommandException(secuboid, "Command not existing", sender, "COMMAND.NOTEXIST", "SECUBOID");
            }

            // Remove page from memory if needed
            if (cv != commands.get("page")) {
                secuboid.getPlayerConf().get(sender).setChatPage(null);
            }

            // Do the command
            InfoCommand ci = cv.getAnnotation(InfoCommand.class);
            CommandExec ce = (CommandExec) cv.getConstructor(Secuboid.class, InfoCommand.class, CommandSender.class,
                    ArgList.class, CommandListener.class).newInstance(secuboid, ci, sender, argList, this);
            if (ce.isExecutable()) {
                ce.commandExecute();
            }

            // a huge number of Exception to catch!
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InstantiationException ex) {
            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (SecurityException ex) {
            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new SecuboidCommandException(secuboid, "General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InvocationTargetException ex) {
            // Catched by SecuboidCommandException
        }
    }

    /**
     * Gets the info command.
     *
     * @param command the command
     * @return command information
     */
    public InfoCommand getInfoCommand(String command) {

        Class<?> infoClass = commands.get(command.toLowerCase());

        if (infoClass == null) {
            return null;
        }

        return infoClass.getAnnotation(InfoCommand.class);
    }
}
