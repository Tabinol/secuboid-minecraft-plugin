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
package me.tabinol.secuboid.selection.region;

import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class ExpandAreaSelection.
 */
public class ExpandAreaSelection extends ActiveAreaSelection implements PlayerMoveListen {

    /**
     * Instantiates a new expand area selection.
     *
     * @param player the player
     */
    public ExpandAreaSelection(Player player) {

        super(player);
    }

    // When we know the area
    /**
     * Instantiates a new expand area selection.
     *
     * @param player the player
     * @param area the area
     */
    public ExpandAreaSelection(Player player, ICuboidArea area) {

        super(player, area);
    }

    // Called from player listenner
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.selection.region.ActiveAreaSelection#playerMove()
     */
    @Override
    public void playerMove() {

        removeSelection();
        Location playerLoc = player.getLocation();
        
        // Check where the player is outside the land
        if(playerLoc.getBlockX() - 1 < area.getX1()) {
            ((CuboidArea) area).setX1(playerLoc.getBlockX() - 1);
        }
        if(playerLoc.getBlockX() + 1 > area.getX2()) {
        	((CuboidArea) area).setX2(playerLoc.getBlockX() + 1);
        }
        if(playerLoc.getBlockZ() - 1 < area.getZ1()) {
        	((CuboidArea) area).setZ1(playerLoc.getBlockZ() - 1);
        }
        if(playerLoc.getBlockZ() + 1 > area.getZ2()) {
        	((CuboidArea) area).setZ2(playerLoc.getBlockZ() + 1);
        }
        
        makeVisualSelection();
    }
}
