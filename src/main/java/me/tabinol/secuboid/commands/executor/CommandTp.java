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
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * The Class Command teleport.
 */
@InfoCommand(name = "tp", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@land" }) //
        })
public final class CommandTp extends CommandExec {

    /**
     * Instantiates a new command teleport.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandTp(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        String curArg = argList.getNext();
        landSelectNullable = secuboid.getLands().getLand(curArg);

        // Land not found
        if (landSelectNullable == null) {
            throw new SecuboidCommandException(secuboid, "On land tp player", player, "COMMAND.TP.LANDNOTFOUND");
        }

        // Check adminmode or permission TP
        checkPermission(true, false, PermissionList.TP.getPermissionType(), null);

        // Try to get Location
        FlagValue value = landSelectNullable.getPermissionsFlags().getFlagAndInherit(FlagList.SPAWN.getFlagType());

        if (value.getValueString().isEmpty()) {
            throw new SecuboidCommandException(secuboid, "On land tp player", player, "COMMAND.TP.NOSPAWN");
        }

        Location location = StringChanges.stringToLocation(value.getValueString());

        if (location == null) {
            throw new SecuboidCommandException(secuboid, "On land tp player", player, "COMMAND.TP.INVALID");
        }

        // Teleport player
        player.teleport(location);
    }
}
