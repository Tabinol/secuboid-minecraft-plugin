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
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * Set spawn command (for land teleports)
 *
 * @author Tabinol
 */
@InfoCommand(name = "setspawn")
public final class CommandSetspawn extends CommandExec {

    /**
     * Instantiates a new command set spawn.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSetspawn(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);

        Location loc = player.getLocation();

        // If the player is not inside the land
        if (!landSelectNullable.isLocationInside(loc)) {
            throw new SecuboidCommandException(secuboid, "On land tp create", player, "COMMAND.TP.OUTSIDE");
        }

        // put player position to String
        String posStr = StringChanges.locationToString(loc);

        // Set flag
        Flag flag = secuboid.getPermissionsFlags().newFlag(FlagList.SPAWN.getFlagType(), posStr, true);
        landSelectNullable.getPermissionsFlags().addFlag(flag);

        player.sendMessage(ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.TP.CREATED"));
    }

}
