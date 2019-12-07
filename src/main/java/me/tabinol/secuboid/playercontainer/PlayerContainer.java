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
package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;

import org.bukkit.entity.Player;

/**
 * The interface PlayerContainer.
 */
public interface PlayerContainer extends Comparable<PlayerContainer> {

    String getName();

    /**
     * Gets the container type.
     *
     * @return the container type
     */
    PlayerContainerType getContainerType();

    /**
     * Return if the player has access from a land.
     *
     * @param player                   the player
     * @param pcLandNullable           The land where this player container come
     *                                 from, owner/resident/... of what land?
     * @param testLandPermissionsFlags The permissions flags (associate to a land or
     *                                 a world) where we want to test the access or
     *                                 where the action is done
     * @return true if the player has access
     */
    boolean hasAccess(Player player, Land pcLandNullable, LandPermissionsFlags testLandPermissionsFlags);

    /**
     * Gets the printable format
     *
     * @return the printable format
     */
    String getPrint();

    /**
     * Convert to file paramater in file save format.
     *
     * @return the file save format
     */
    String toFileFormat();
}
