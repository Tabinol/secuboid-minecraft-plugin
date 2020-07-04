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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;

/**
 * The Class CommandRemove.
 */
@InfoCommand(name = "remove", //
        completion = { //
                @CompletionMap(regex = "^$", completions = {"force", "recursive"}) //
        })
public final class CommandRemove extends CommandCollisionsThreadExec {

    /**
     * Instantiates a new command remove.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandRemove(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
                         final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);

        final String fonction = argList.getNext();
        final LandAction landAction;
        if (fonction == null) {
            landAction = LandAction.LAND_REMOVE;
        } else if (fonction.equalsIgnoreCase("force")) {
            landAction = LandAction.LAND_REMOVE_FORCE;
        } else if (fonction.equalsIgnoreCase("recursive")) {
            landAction = LandAction.LAND_REMOVE_RECURSIVE;
        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", sender, "GENERAL.MISSINGINFO");
        }

        // Check for collision
        checkCollision(landSelectNullable.getWorldName(), landSelectNullable.getName(), landSelectNullable, null,
                landAction, 0, null, landSelectNullable.getParent(), landSelectNullable.getOwner(), true);
    }

    @Override
    public void commandThreadExecute(final Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            return;
        }

        new CommandCancel(secuboid, null, sender, argList).commandExecute();
        playerConf.setConfirm(new ConfirmEntry(ConfirmEntry.ConfirmType.REMOVE_LAND, landSelectNullable, 0,
                collisions.getAction()));
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.CONFIRM"));
    }
}
