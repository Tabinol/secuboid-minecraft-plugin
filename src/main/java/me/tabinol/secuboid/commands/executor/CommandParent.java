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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;

/**
 * The parent command.
 */
@InfoCommand(name = "parent", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@land", "unset" }) //
        })
public final class CommandParent extends CommandCollisionsThreadExec {

    /**
     * Create a parent command.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandParent(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);

        String curArg = argList.getNext();
        Land parent = null;

        if (!curArg.equalsIgnoreCase("unset")) {
            parent = secuboid.getLands().getLand(curArg);

            // Check if the parent exist
            if (parent == null) {
                throw new SecuboidCommandException(secuboid, "CommandParent", player, "COMMAND.PARENT.INVALID");
            }

            // Check if the land is a children
            if (landSelectNullable.isDescendants(parent)) {
                throw new SecuboidCommandException(secuboid, "CommandParent", player, "COMMAND.PARENT.NOTCHILD");
            }
        }

        // Check for collision
        checkCollision(landSelectNullable.getWorldName(), landSelectNullable.getName(), landSelectNullable, null, LandAction.LAND_PARENT, 0, null, parent,
                landSelectNullable.getOwner(), true);
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            return;
        }

        // Set parent
        landSelectNullable.setParent(parent);
        if (parent == null) {
            player.sendMessage(
                    ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.PARENT.REMOVEDONE"));
        } else {
            player.sendMessage(ChatColor.GREEN + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.PARENT.DONE", parent.getName()));
        }
    }
}
