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
import java.util.UUID;

import me.tabinol.secuboidapi.exceptions.ApiSecuboidLandException;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.Location;

/**
 * The Interface ApiLands. Can give access to all lands.
 */
public interface ApiLands {

    /**
     * Creates the land. (without parent)
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @return the land
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public ApiLand createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area)
            throws ApiSecuboidLandException;
    // For Land with parent
    /**
     * Creates the child land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @param parent the parent
     * @return the land
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public ApiLand createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area, ApiLand parent)
            throws ApiSecuboidLandException;

    /**
     * Checks if is name exist.
     *
     * @param landName the land name
     * @return true, if is name exist
     */
    public boolean isNameExist(String landName);
    
    /**
     * Removes the land.
     *
     * @param land the land
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean removeLand(ApiLand land) throws ApiSecuboidLandException;

    /**
     * Removes the land.
     *
     * @param landName the land name
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean removeLand(String landName) throws ApiSecuboidLandException;
    
    /**
     * Removes the land.
     *
     * @param uuid the uuid
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean removeLand(UUID uuid) throws ApiSecuboidLandException;

    /**
     * Rename land.
     *
     * @param landName the land name
     * @param newName the new name
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean renameLand(String landName, String newName) throws ApiSecuboidLandException;
    
    /**
     * Rename land.
     *
     * @param uuid the uuid
     * @param newName the new name
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean renameLand(UUID uuid, String newName) throws ApiSecuboidLandException;

    /**
     * Rename land.
     *
     * @param land the land
     * @param newName the new name
     * @return true, if successful
     * @throws ApiSecuboidLandException the secuboid land exception
     */
    public boolean renameLand(ApiLand land, String newName) throws ApiSecuboidLandException;
    
    /**
     * Gets the land from name.
     *
     * @param landName the land name
     * @return the land
     */
    public ApiLand getLand(String landName);

    /**
     * Gets the land from UUID.
     *
     * @param uuid the uuid
     * @return the land
     */
    public ApiLand getLand(UUID uuid);

    /**
     * Gets the land from location.
     *
     * @param loc the loc
     * @return the land
     */
    public ApiLand getLand(Location loc);
    
    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public Collection<ApiLand> getLands();
    
    /**
     * Gets the land or outside area (Dummy Land World).
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public ApiDummyLand getLandOrOutsideArea(Location loc);

    /**
     * Gets the outside area (Dummy Land World).
     *
     * @param loc the loc
     * @return the outside area
     */
    public ApiDummyLand getOutsideArea(Location loc);

    /**
     * Gets the outside area (Dummy Land World).
     *
     * @param worldName the world name
     * @return the outside area
     */
    public ApiDummyLand getOutsideArea(String worldName);

    /**
     * Gets the lands (Dummy Land World).
     *
     * @param loc the loc
     * @return the lands
     */
    public Collection<ApiLand> getLands(Location loc);

    /**
     * Gets the lands.
     *
     * @param owner the owner
     * @return the lands
     */
    public Collection<ApiLand> getLands(ApiPlayerContainer owner);


    /**
     * Gets the lands from type.
     *
     * @param type the type
     * @return the lands
     */
    public Collection<ApiLand> getLands(ApiType type);
    
    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public Collection<ApiCuboidArea> getCuboidAreas(Location loc);
    
    /**
     * Gets the cuboid area.
     *
     * @param loc the loc
     * @return the cuboid area
     */
    public ApiCuboidArea getCuboidArea(Location loc);
    
    /**
     * Gets the lands for sale.
     *
     * @return the lands for sale
     */
    public Collection<ApiLand> getForSale();

    
    /**
     * Gets the lands for rent.
     *
     * @return the lands for rent
     */
    public Collection<ApiLand> getForRent();
}
