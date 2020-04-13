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
import me.tabinol.secuboid.config.BannedWords;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.collisions.Collisions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandRename.
 */
@InfoCommand(name = "rename", forceParameter = true)
public final class CommandRename extends CommandCollisionsThreadExec {

    /**
     * Instantiates a new command rename.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandRename(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);

        String curArg = argList.getNext();
        if (BannedWords.isBannedWord(curArg)) {
            throw new SecuboidCommandException(secuboid, "CommandRename", player, "COMMAND.RENAME.HINTUSE");
        }

        // Check for collision
        checkCollision(landSelectNullable.getWorldName(), curArg, landSelectNullable, null, Collisions.LandAction.LAND_RENAME, 0,
                null, landSelectNullable.getParent(), landSelectNullable.getOwner(), true);
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            return;
        }

        String oldName = landSelectNullable.getName();
        String newName = collisions.getLandName();

        try {
            secuboid.getLands().renameLand(oldName, newName);
        } catch (SecuboidLandException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "On land rename", player, "GENERAL.ERROR");
        }
        player.sendMessage(ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.RENAME.ISDONE", oldName, newName));

        // Cancel the selection
        new CommandCancel(secuboid, null, sender, argList).commandExecute();
    }
}
