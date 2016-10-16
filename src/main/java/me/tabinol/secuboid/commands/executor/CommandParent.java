/*
 Secuboid: Lands and Factions plugin for Minecraft server
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

import me.tabinol.secuboid.commands.CommandCollisionsThreadExec;
import me.tabinol.secuboid.lands.collisions.Collisions;
import org.bukkit.ChatColor;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;

@InfoCommand(name="parent", forceParameter=true)
public class CommandParent extends CommandCollisionsThreadExec {
    
    public CommandParent(CommandEntities entity) throws SecuboidCommandException {
        
        super(entity);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        
        String curArg = entity.argList.getNext();
        Land parent = null;
        
        if(!curArg.equalsIgnoreCase("unset")) {
            parent = (Land) Secuboid.getThisPlugin().getLands().getLand(curArg);
            
            // Check if the parent exist
            if (parent == null) {
                throw new SecuboidCommandException("CommandParent", entity.player, "COMMAND.PARENT.INVALID");
            }
            
            // Check if the land is a children
            if(land.isDescendants(parent)) {
                throw new SecuboidCommandException("CommandParent", entity.player, "COMMAND.PARENT.NOTCHILD");
            }
        }
        
        // Check for collision
        checkCollision(land.getName(), land, null, LandAction.LAND_PARENT, 0, null, parent,
                land.getOwner(), true);
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            return;
        }

        // Set parent
        land.setParent(parent);
        if(parent == null) {
            entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.PARENT.REMOVEDONE"));
            Secuboid.getThisPlugin().getLog().write(entity.playerName + " has set land " + land.getName() + " to no parent ");
        } else {
            entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.PARENT.DONE", parent.getName()));
            Secuboid.getThisPlugin().getLog().write(entity.playerName + " has set land " + land.getName() + " to parent " + parent.getName());
        }
    }
}
