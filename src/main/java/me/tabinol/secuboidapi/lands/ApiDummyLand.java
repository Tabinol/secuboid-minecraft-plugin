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
package me.tabinol.secuboidapi.lands;

import java.util.Collection;
import java.util.Set;

import me.tabinol.secuboidapi.parameters.*;
import me.tabinol.secuboidapi.parameters.ApiFlagType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The Interface ApiDummyLand. Represent a word in a non protected land.
 */
public interface ApiDummyLand {

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    public String getWorldName();
    
    /**
     * Gets the world.
     *
     * @return the world
     */
    public World getWorld();
    
    /**
     * Copy permissions and flags from this land to an other.
     * Duplicated permissions and flags will be overwrite.
     * @param desLand The destination land or dummy land.
     */
    public void copyPermsFlagsTo(ApiDummyLand desLand);
    
    /**
     * Gets the sets of player containers having a permission.
     *
     * @return the sets the player containers
     */
    public Set<ApiPlayerContainer> getSetPCHavePermission();
    
    /**
     * Gets the permissions list for a player container.
     *
     * @param pc the player container
     * @return the permissions for player container
     */
    public Collection<ApiPermission> getPermissionsForPC(ApiPlayerContainer pc);
    
    /**
     * Check if a player has permission with inheritance.
     *
     * @param player the player
     * @param pt the permission type 
     * @return the permission result or default value
     */
    public boolean checkPermissionAndInherit(Player player, ApiPermissionType pt);
    
    /**
     * Check if a player has permission without inheritance.
     *
     * @param player the player
     * @param pt the permission type
     * @return the permission result or default value
     */
    public boolean checkPermissionNoInherit(Player player, ApiPermissionType pt);
    
    /**
     * Gets the flags.
     *
     * @return the flags
     */
    public Collection<ApiLandFlag> getFlags();
    
    /**
     * Gets the flags with inheritance.
     *
     * @param ft the flag type
     * @return the flags value or default value
     */
    public ApiFlagValue getFlagAndInherit(ApiFlagType ft);

    /**
     * Gets the flags without inheritance.
     *
     * @param ft the flag type
     * @return the flag value or default value
     */
    public ApiFlagValue getFlagNoInherit(ApiFlagType ft);
}
