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
package me.tabinol.secuboid.utilities;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Players utilities
 *
 * @author tabinol
 */
public class PlayersUtil {

    /**
     * Gets the y near player before air.
     *
     * @param player the player
     * @param x      the x
     * @param z      the z
     * @return the y near player
     */
    public static int getYNearPlayer(Player player, int x, int z) {
        Location loc = new Location(player.getWorld(), x, player.getLocation().getY() - 1, z);

        if (!loc.getBlock().getType().isSolid()) {
            while (!loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
                    && loc.getBlockY() > 1) {
                loc.subtract(0, 1, 0);
            }
        } else {
            while (loc.getBlock().getType().isSolid() && loc.getBlockY() < player.getWorld().getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }
}
