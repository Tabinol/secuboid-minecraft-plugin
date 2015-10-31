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
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.ExpandAreaSelection;

import org.bukkit.ChatColor;


/**
 * The Class CommandExpand.
 */
@InfoCommand(name="expand")
public class CommandExpand extends CommandExec {

    /**
     * Instantiates a new command expand.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandExpand(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(null, null);
        // checkPermission(false, false, null, null);

        ApiLand land = entity.playerConf.getSelection().getLand();
        String curArg = entity.argList.getNext();

        if (curArg == null) {

            if (entity.playerConf.getSelection().getSelection(SelectionType.AREA) instanceof ExpandAreaSelection) {
                throw new SecuboidCommandException("Player Expand", entity.player, "COMMAND.EXPAND.ALREADY");
            }

            entity.player.sendMessage(ChatColor.GRAY + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            Secuboid.getThisPlugin().getLog().write(entity.player.getName() + " have join ExpandMode.");

            // Check the selection before (if exist)
            ApiCuboidArea area = entity.playerConf.getSelection().getCuboidArea();

            if (area == null && land != null && (area = land.getArea(1)) != null) {

                // Expand an existing area?
                entity.playerConf.getSelection().setAreaToReplace(area);
            }

            if (area == null) {
                entity.playerConf.getSelection().addSelection(new ExpandAreaSelection(entity.player));
            } else {
                entity.playerConf.getSelection().addSelection(new ExpandAreaSelection(entity.player, area.copyOf()));
            }

        } else if (curArg.equalsIgnoreCase("done")) {

            // Expand done
            entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            Secuboid.getThisPlugin().getLog().write(entity.playerName + " have quit ExpandMode.");

            ApiCuboidArea area = entity.playerConf.getSelection().getCuboidArea();
            if (area != null) {

                entity.playerConf.getSelection().addSelection(new AreaSelection(entity.player, area));

                if (!((AreaSelection) entity.playerConf.getSelection().getSelection(SelectionType.AREA)).getCollision()) {
                    entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                            + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
                } else {
                    entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.RED
                            + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                }
            }

        } else {
            throw new SecuboidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
