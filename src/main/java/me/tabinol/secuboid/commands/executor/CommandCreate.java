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
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.BannedWords;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;

/**
 * The Class CommandCreate.
 */
@InfoCommand(name = "create", forceParameter = true)
public final class CommandCreate extends CommandCollisionsThreadExec {

    /**
     * Instantiates a new command create.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCreate(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(null, true);

        final AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);

        final Area area = select.getVisualSelection().getArea();
        final String curArg = argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new SecuboidCommandException(secuboid, "CommandCreate", player, "COMMAND.CREATE.HINTUSE");
        }

        final LandCheckValues landCheckValues = landCheckForCreate(select);
        checkCollision(area.getWorldName(), curArg, null, landCheckValues.localType, LandAction.LAND_ADD, 0, area,
                landCheckValues.realLocalParent, landCheckValues.localOwner, true);
    }

    @Override
    public void commandThreadExecute(final Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            new CommandCancel(secuboid, null, sender, argList).commandExecute();
            return;
        }

        // Create Land
        try {
            final Land cLand = secuboid.getLands().createLand(collisions.getLandName(), owner, newArea, parent,
                    collisions.getPrice(), type);

            player.sendMessage(
                    ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.CREATE.DONE"));

            // Cancel and select the land
            new CommandCancel(secuboid, null, sender, argList).commandExecute();
            new CommandSelect(secuboid, null, player,
                    new ArgList(secuboid, new String[] { "land", cLand.getName() }, player)).commandExecute();

        } catch (final SecuboidLandException ex) {
            throw new SecuboidCommandException(secuboid, sender,
                    String.format("Error creating land \"%s\" for \"%s\"", collisions.getLandName(), owner.getName()),
                    ex);
        }
    }
}
