/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandHelp.
 */
@InfoCommand(name = "help", allowConsole = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@command" }) //
        })
public final class CommandHelp extends CommandExec {

    /**
     * The command name.
     */
    private String commandName;

    /**
     * Instantiates a new command help.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandHelp(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {
        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        String arg = argList.getNext();

        if (arg == null) {
            commandName = "GENERAL";
        } else {
            // Will throw an exception if the command name is invalid
            try {
                InfoCommand infoCommandLocal = secuboid.getCommandListener().getInfoCommand(arg);
                if (infoCommandLocal != null) {
                    commandName = infoCommandLocal.name().toUpperCase();
                } else {
                    // Invalid command, just arg and will run Exception with showHelp()
                    commandName = arg;
                }
            } catch (IllegalArgumentException ex) {
                commandName = "GENERAL";
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

        String help = secuboid.getLanguage().getHelp("SECUBOID", commandName);

        // If there is no help for this command
        if (help == null) {
            throw new SecuboidCommandException(secuboid, "Command with no help", sender, "HELP.NOHELP");
        }

        if (commandName.equals("GENERAL")) {
            new ChatPage(secuboid, "HELP.LISTSTART", help, sender, null).getPage(1);
        } else {
            sender.sendMessage(help);
        }
    }

}
