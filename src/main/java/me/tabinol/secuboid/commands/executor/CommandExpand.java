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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandExpand.
 */
@InfoCommand(name = "expand")
public class CommandExpand extends CommandExec {

    /**
     * Instantiates a new command expand.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandExpand(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(null, null);
        // checkPermission(false, false, null, null);

        RealLand selLand = playerConf.getSelection().getLand();
        String curArg = argList.getNext();

        if (curArg == null) {

            if (playerConf.getSelection().getSelection(SelectionType.AREA) instanceof AreaSelection
                    && ((AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA)).getMoveType() == AreaSelection.MoveType.EXPAND) {
                throw new SecuboidCommandException(secuboid, "Player Expand", player, "COMMAND.EXPAND.ALREADY");
            }

            player.sendMessage(ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.EXPAND.HINT",
                    ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            secuboid.getLog().write(player.getName() + " have join ExpandMode.");

            // Check the selection before (if exist)
            Area area = playerConf.getSelection().getArea();

            if (area == null && selLand != null && (area = selLand.getArea(1)) != null) {

                // Expand an existing area?
                playerConf.getSelection().setAreaToReplace(area);
            }

            if (area == null) {
                playerConf.getSelection().addSelection(new AreaSelection(secuboid, player, null, false,
                        AreaType.CUBOID, AreaSelection.MoveType.EXPAND));
            } else {
                playerConf.getSelection().addSelection(new AreaSelection(secuboid, player, area.copyOf(), false,
                        AreaType.CUBOID, AreaSelection.MoveType.EXPAND));
            }

        } else if (curArg.equalsIgnoreCase("done")) {

            // Expand done
            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            secuboid.getLog().write(playerName + " have quit ExpandMode.");

            Area area = playerConf.getSelection().getArea();
            if (area != null) {

                playerConf.getSelection().addSelection(new AreaSelection(secuboid, player, area, false, AreaType.CUBOID,
                        AreaSelection.MoveType.PASSIVE));

                if (!((AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA)).getVisualSelection().hasCollision()) {
                    player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                            + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
                } else {
                    player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.RED
                            + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                }
            }

        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", player, "GENERAL.MISSINGINFO");
        }
    }
}
