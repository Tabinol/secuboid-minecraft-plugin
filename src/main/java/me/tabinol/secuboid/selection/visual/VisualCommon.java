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
package me.tabinol.secuboid.selection.visual;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import org.bukkit.Location;

/**
 * Visual selection common methods.
 */
class VisualCommon {

    private final Secuboid secuboid;
    private final PlayerConfEntry entry;
    private int y1;
    private int y2;

    /**
     * Creates a visual selection common method for a new area.
     *
     * @param secuboid       the Secuboid instance
     * @param entry          the player entry
     * @param playerLocation the player location
     */
    VisualCommon(Secuboid secuboid, PlayerConfEntry entry, Location playerLocation) {
        this.secuboid = secuboid;
        this.entry = entry;
        y1 = Integer.MAX_VALUE;
        y2 = Integer.MIN_VALUE;
        setBottomTop(playerLocation);
    }

    /**
     * Creates a visual selection common method for an existing area.
     *
     * @param secuboid the Secuboid instance
     * @param entry    the player entry
     * @param y1       the y1
     * @param y2       the y2
     */
    VisualCommon(Secuboid secuboid, PlayerConfEntry entry, int y1, int y2) {
        this.secuboid = secuboid;
        this.entry = entry;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Update top and bottom positions.
     *
     * @param playerLocation the player location
     */
    final void setBottomTop(Location playerLocation) {
        int playerBottom = entry.getSelectionBottom();
        int maxBottom = secuboid.getConf().getMaxBottom();
        int playerTop = entry.getSelectionTop();
        int maxTop = secuboid.getConf().getMaxTop();
        int playerY = playerLocation.getBlockY();
        if (playerBottom >= 0 && playerBottom < y1) {
            y1 = playerBottom < maxBottom ? maxBottom : playerBottom;
        } else {
            int newY1 = playerY + playerBottom;
            if (y1 < newY1) {
                y1 = newY1 < maxBottom ? maxBottom : newY1;
            }
        }
        if (playerTop >= 0 && playerTop > y2) {
            y2 = playerTop > maxTop ? maxTop : playerTop;
        } else {
            int newY2 = playerY - playerTop;
            if (y2 > newY2) {
                y2 = newY2 > maxTop ? maxTop : newY2;
            }
        }
    }

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    int getY1() {
        return y1;
    }

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    int getY2() {
        return y2;
    }
}
