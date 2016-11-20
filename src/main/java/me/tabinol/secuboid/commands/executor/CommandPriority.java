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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandPriority.
 */
@InfoCommand(name = "priority", forceParameter = true)
public class CommandPriority extends CommandExec {

    /**
     * Instantiates a new command priority.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandPriority(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);
        String curArg = argList.getNext();
        short newPrio;

        if (land.getParent() != null) {
            throw new SecuboidCommandException(secuboid, "Priority", player, "COMMAND.PRIORITY.NOTCHILD");
        }
        try {
            newPrio = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            throw new SecuboidCommandException(secuboid, "Priority", player, "COMMAND.PRIORITY.INVALID", RealLand.MINIM_PRIORITY + "", RealLand.MAXIM_PRIORITY + "");
        }
        if (!Calculate.isInInterval(newPrio, RealLand.MINIM_PRIORITY, RealLand.MAXIM_PRIORITY)) {
            throw new SecuboidCommandException(secuboid, "Priority", player, "COMMAND.PRIORITY.INVALID", RealLand.MINIM_PRIORITY + "", RealLand.MAXIM_PRIORITY + "");
        }
        land.setPriority(newPrio);

        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage(
                "COMMAND.PRIORITY.DONE", land.getName(), land.getPriority() + ""));
        secuboid.getLog().write("Priority for land " + land.getName() + " changed for " + land.getPriority());
    }
}
