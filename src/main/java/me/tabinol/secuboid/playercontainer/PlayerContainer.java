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

import me.tabinol.secuboid.lands.RealLand;
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
     * Return if the player has access
     *
     * @param player the player
     * @return true if the player has access
     */
    boolean hasAccess(Player player);

    /**
     * Return if the player has access from a land
     *
     * @param player the player
     * @param land the land
     * @return true if the player has access
     */
    boolean hasAccess(Player player, RealLand land);

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

    /**
     * Gets the land.
     *
     * @return the land
     */
    RealLand getLand();

    /**
     * Sets the land. Not in Common API for security.
     *
     * @param land the new land
     */
    void setLand(RealLand land);
}
