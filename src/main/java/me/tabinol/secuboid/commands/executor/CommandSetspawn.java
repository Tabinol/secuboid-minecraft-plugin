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

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.parameters.FlagList;
import me.tabinol.secuboid.parameters.LandFlag;
import me.tabinol.secuboidapi.utilities.StringChanges;

@InfoCommand(name="setspawn")
public class CommandSetspawn extends CommandExec {

    /**
     * Instantiates a new command set spawn.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSetspawn(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        
        Location loc = entity.player.getLocation();
        
        // If the player is not inside the land
        if(!land.isLocationInside(loc)) {
            throw new SecuboidCommandException("On land tp create", entity.player, "COMMAND.TP.OUTSIDE");
        }
        
        // put player position to String
        String posStr = StringChanges.locationToString(loc);
        
        // Set flag
        LandFlag flag = new LandFlag(FlagList.SPAWN.getFlagType(), posStr, true);
        ((Land) land).addFlag(flag);
        
        entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.TP.CREATED"));
    }

}
