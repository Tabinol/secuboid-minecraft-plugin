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

import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.parameters.FlagList;
import me.tabinol.secuboid.parameters.FlagValue;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboid.utilities.StringChanges;


/**
 * The Class CommandTp.
 */
@InfoCommand(name="tp", forceParameter=true)
public class CommandTp extends CommandExec {

    /**
     * Instantiates a new command tp.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandTp(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        String curArg = entity.argList.getNext();
        land = Secuboid.getThisPlugin().getLands().getLand(curArg);
        
        // Land not found
        if(land == null) {
            throw new SecuboidCommandException("On land tp player", entity.player, "COMMAND.TP.LANDNOTFOUND");
        }
        
        // Check adminmod or permission TP
        checkPermission(true, false, PermissionList.TP.getPermissionType(), null);

        // Try to get Location
        FlagValue value = land.getFlagAndInherit(FlagList.SPAWN.getFlagType());
        
        if(value.getValueString().isEmpty()) {
            throw new SecuboidCommandException("On land tp player", entity.player, "COMMAND.TP.NOSPAWN");
        }
        
        Location location = StringChanges.stringToLocation(value.getValueString());
        
        if(location == null) {
            throw new SecuboidCommandException("On land tp player", entity.player, "COMMAND.TP.INVALID");
        }
        
        // Teleport player
        entity.player.teleport(location);
    }
}
