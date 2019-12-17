/*
 Secuboid: Lands plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

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
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.types.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The type command class.
 */
@InfoCommand(name = "type", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "remove", "list", "@type" }), //
        })
public final class CommandType extends CommandExec {

    /**
     * Instantiates a new command type.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandType(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);

        String curArg = argList.getNext();

        if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            for (Type type : secuboid.getTypes().getTypes()) {
                if (stList.length() != 0) {
                    stList.append(" ");
                }
                stList.append(ChatColor.WHITE).append(type.getName());
                stList.append(Config.NEWLINE);
            }
            new ChatPage(secuboid, "COMMAND.TYPES.LISTSTART", stList.toString(), player, null).getPage(1);

        } else if (curArg.equals("remove")) {

            landSelectNullable.setType(null);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.TYPES.REMOVEISDONE", landSelectNullable.getName()));

        } else { // Type change

            Type type = secuboid.getTypes().getType(curArg);

            if (type == null) {
                throw new SecuboidCommandException(secuboid, "Land Types", player, "COMMAND.TYPES.INVALID");
            }

            landSelectNullable.setType(type);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.TYPES.ISDONE", type.getName(), landSelectNullable.getName()));
        }
    }
}
