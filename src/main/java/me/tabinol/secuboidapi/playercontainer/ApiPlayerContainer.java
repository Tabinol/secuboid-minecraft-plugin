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
package me.tabinol.secuboidapi.playercontainer;

import me.tabinol.secuboidapi.lands.ApiLand;

import org.bukkit.entity.Player;


/**
 * The Interface PlayerContainerInterface. Represent a player container of
 * any types.
 */
public interface ApiPlayerContainer {
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName();
    
    /**
     * Gets the container type.
     *
     * @return the container type
     */
    public ApiPlayerContainerType getContainerType();

    /**
     * Equals.
     *
     * @param container2 the container2
     * @return true, if successful
     */
    public boolean equals(ApiPlayerContainer container2);
    
    /**
     * Copy of.
     *
     * @return the player container
     */
    public ApiPlayerContainer copyOf();
    
    /**
     * Checks if the player has access or is member of this player container.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean hasAccess(Player player);
    
    /**
     * Check if the player has access or is member of this player container.
     * The land option is for assume the player to check if the player is
     * owner/resident/... of this specific land. This method is only used
     * from a DummyLand (Default or World)
     * 
     * @param player the player
     * @param land from what land?
     * @return true, if successful
     */
    public boolean hasAccess(Player player, ApiLand land);
    
    /**
     * Gets the printable output.
     *
     * @return the printable output
     */
    public String getPrint();
    
}
