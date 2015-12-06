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

package me.tabinol.secuboidapi.lands.areas;

import java.util.Collection;

import me.tabinol.secuboidapi.lands.ApiLand;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a Cuboid area type.
 */
public interface ApiCuboidArea extends ApiArea {

    /**
     * Equals.
     *
     * @param area2 the area2
     * @return true, if successful
     */
    public boolean equals(ApiCuboidArea area2);
    
    /**
     * Copy of.
     *
     * @return the cuboid area
     */
    public ApiCuboidArea copyOf();
    
    /**
     * Convert the cuboid area to string.
     *
     * @return the stirng
     */
    @Override
    public String toString();
    
    /**
     * Gets the prints the.
     *
     * @return the prints the
     */
    public String getPrint();
    
    /**
     * Gets the key. If the cuboid area is not part of a land, the value is 0.
     *
     * @return the key
     */
    public Integer getKey();
    
    /**
     * Gets the total block number of blocks.
     *
     * @return the total block
     */
    public long getTotalBlock();
    
    /**
     * Checks if there is a collision.
     *
     * @param area2 the area2
     * @return true, if is collision
     */
    public boolean isCollision(ApiCuboidArea area2);
    
    /**
     * Checks if is the location is inside the cuboid area.
     *
     * @param loc the loc
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc);
    
    /**
     * Check if there is a collision and create an area of the collision.
     * @param area2 The second area to compare
     * @return the CuboidArea collision or null if there is no collision
     */
    public ApiCuboidArea getCollisionArea(ApiCuboidArea area2);
    
    /**
     * Create a collection of outside areas. THE RETURN VALUE IS NOT EXACT!
     * @param area2 Area to compare
     * @return A collection of outside areas
     */
    public Collection<ApiCuboidArea> getOutside(ApiCuboidArea area2);
    
    /**
     * Gets the land.
     *
     * @return the land or null if the cuboid area is not from a land.
     */
    public ApiLand getLand();
    
    /**
     * Gets the world name.
     *
     * @return the world name
     */
    public String getWorldName();
    
    /**
     * Gets the word.
     *
     * @return the word
     */
    public World getWord();
    
    /**
     * Gets the x1.
     *
     * @return the x1
     */
    public int getX1();
    
    /**
     * Gets the y1.
     *
     * @return the y1
     */
    public int getY1();
    
    /**
     * Gets the z1.
     *
     * @return the z1
     */
    public int getZ1();
    
    /**
     * Gets the x2.
     *
     * @return the x2
     */
    public int getX2();
    
    /**
     * Gets the y2.
     *
     * @return the y2
     */
    public int getY2();
    
    /**
     * Gets the z2.
     *
     * @return the z2
     */
    public int getZ2();
}
