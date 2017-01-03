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
package me.tabinol.secuboid.lands;

import org.bukkit.entity.Player;

/**
 * Represents every lands (global or real lands).
 *
 * @author Tabinol
 */
public interface Land {

    /**
     * Land class type (not the Secuboid land type).
     */
    enum LandType {
        /**
         * global world land
         */
        WORLD,
        /**
         * default values land
         */
        DEFAULT,

        /**
         * real land
         */
        REAL
    }

    /**
     * Gets the world name (or null for a Default land).
     *
     * @return the world name
     */
    String getWorldName();

    /**
     * Gets if this land class type (not the Secuboid land type).
     *
     * @return land class type
     */
     LandType getLandType();

    /**
     * Gets the permissions and flags.
     *
     * @return the permissions and flags
     */
    LandPermissionsFlags getPermissionsFlags();

    /**
     * Gets if the player is banned. from a global land, this method always returns false.
     *
     * @param player the player
     * @return true if banned
     */
    boolean isBanned(Player player);
}
