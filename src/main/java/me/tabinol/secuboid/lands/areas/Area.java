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
package me.tabinol.secuboid.lands.areas;

import me.tabinol.secuboid.lands.RealLand;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a area of any type (abstract).
 */
public interface Area extends Comparable<Area> {

    /**
     * Transforms the area to file format.
     *
     * @return a string
     */
    String toFileFormat();

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    String getPrint();

    /**
     * Gets the area type.
     *
     * @return the area type
     */
    AreaType getAreaType();

    /**
     * Gets the key from the land.
     *
     * @return the key
     */
    Integer getKey();

    /**
     * Sets the land. Internal use only.
     *
     * @param land the new land
     */
    void setLand(RealLand land);

    /**
     * Gets the land.
     *
     * @return the land
     */
    RealLand getLand();

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    String getWorldName();

    /**
     * Gets the word.
     *
     * @return the word
     */
    World getWord();

    /**
     * Gets the x1.
     *
     * @return the x1
     */
    int getX1();

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    int getY1();

    /**
     * Gets the z1.
     *
     * @return the z1
     */
    int getZ1();

    /**
     * Gets the x2.
     *
     * @return the x2
     */
    int getX2();

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    int getY2();

    /**
     * Gets the z2.
     *
     * @return the z2
     */
    int getZ2();

    /**
     * Gets the volume.
     *
     * @return the volume
     */
    long getVolume();

    /**
     * Gets if the locatation is inside the area.
     *
     * @param worldName the world name
     * @param x the x
     * @param y the y
     * @param z the z
     * @return if true or false
     */
    public abstract boolean isLocationInside(String worldName, int x, int y, int z);

    /**
     * Gets if the locatation is inside the area.
     *
     * @param loc the location
     * @return if true or false
     */
    public boolean isLocationInside(Location loc);

    /**
     * Gets an exact copy of this area.
     *
     * @return an area copy
     */
    public Area copyOf();
}
