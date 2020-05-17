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

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.AreaSelection.MoveType;

/**
 * The Class CommandSelectWorldedit. WorldEdit is in a separate class from
 * CommandSelect because if WorldEdit is not installed, we don't want to makes
 * error.
 */
class CommandSelectWorldedit {

    private final Secuboid secuboid;

    /**
     * The player.
     */
    private final Player player;

    /**
     * The entry.
     */
    private final PlayerConfEntry entry;

    /**
     * Instantiates a new command select worldedit.
     *
     * @param secuboid secuboid instance
     * @param player   the player
     * @param entry    the entry
     * @throws SecuboidCommandException the secuboid command exception
     */
    CommandSelectWorldedit(final Secuboid secuboid, final Player player, final PlayerConfEntry entry)
            throws SecuboidCommandException {

        this.secuboid = secuboid;
        this.player = player;
        this.entry = entry;
    }

    /**
     * Make select.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    void MakeSelect() throws SecuboidCommandException {

        if (secuboid.getDependPlugin().getWorldEdit() == null) {
            throw new SecuboidCommandException(secuboid, "CommandSelectWorldEdit", player,
                    "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
        }
        final LocalSession session = ((WorldEditPlugin) secuboid.getDependPlugin().getWorldEdit()).getSession(player);

        try {
            final Region sel;
            if (session.getSelectionWorld() == null
                    || !((sel = session.getSelection(session.getSelectionWorld())) != null
                            && (sel instanceof CuboidRegion || sel instanceof CylinderRegion))) {
                throw new SecuboidCommandException(secuboid, "CommandSelectWorldEdit", player,
                        "COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED");
            }

            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));

            final AreaSelection select;
            if (sel instanceof CuboidRegion) {
                select = new AreaSelection(secuboid, player,
                        new CuboidArea(false, player.getWorld().getName(), sel.getMinimumPoint().getBlockX(),
                                sel.getMinimumPoint().getBlockY(), sel.getMinimumPoint().getBlockZ(),
                                sel.getMaximumPoint().getBlockX(), sel.getMaximumPoint().getBlockY(),
                                sel.getMaximumPoint().getBlockZ()),
                        null, true, AreaType.CUBOID, MoveType.PASSIVE);
            } else {
                select = new AreaSelection(secuboid, player,
                        new CylinderArea(false, player.getWorld().getName(), sel.getMinimumPoint().getBlockX(),
                                sel.getMinimumPoint().getBlockY(), sel.getMinimumPoint().getBlockZ(),
                                sel.getMaximumPoint().getBlockX(), sel.getMaximumPoint().getBlockY(),
                                sel.getMaximumPoint().getBlockZ()),
                        null, true, AreaType.CYLINDER, MoveType.PASSIVE);
            }

            entry.getSelection().addSelection(select);
            entry.setAutoCancelSelect(true);

        } catch (final IncompleteRegionException ex) {
            throw new SecuboidCommandException(secuboid, "CommandSelectWorldEdit", player,
                    "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
