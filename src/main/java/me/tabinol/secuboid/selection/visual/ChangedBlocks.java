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
package me.tabinol.secuboid.selection.visual;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

/**
 * Represents changes blocks for selection.
 *
 * @author tabinol
 */
final class ChangedBlocks {

    /**
     * Maximum visible distance in blocks.
     */
    public static final int MAX_DISTANCE = 128;
    public static final Material SEL_ACTIVE = Material.SPONGE;
    public static final Material SEL_COLLISION = Material.REDSTONE_BLOCK;
    public static final Material SEL_PASSIVE = Material.IRON_BLOCK;

    private final Player player;

    /**
     * The block list.
     */
    private final Map<Location, BlockData> locationToBlockData;


    ChangedBlocks(Player player) {
        this.player = player;
        locationToBlockData = new HashMap<>();
    }

    void changeBlock(Location location, BlockData blockData) {
        if (!isDistanceMoreThan(player.getLocation(), location, MAX_DISTANCE)) {
            BlockData currentBlockData = location.getBlock().getBlockData();
            locationToBlockData.put(location, currentBlockData);
            player.sendBlockChange(location, blockData);
        }
    }

    void resetBlocks() {
        for (Map.Entry<Location, BlockData> entrySet : this.locationToBlockData.entrySet()) {
            player.sendBlockChange(entrySet.getKey(), entrySet.getValue());
        }
        locationToBlockData.clear();
    }

    /**
     * Gets if the distance between two locations is more than "distance". This method looks only X and Z axes and
     * ignore angle.
     *
     * @param loc1     the first location
     * @param loc2     the second location
     * @param distance the distance
     * @return the result (true if outside of the distance)
     */
    private boolean isDistanceMoreThan(Location loc1, Location loc2, int distance) {
        return Math.abs(loc1.getBlockX() - loc2.getBlockX()) > distance || Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) > distance;
    }
}