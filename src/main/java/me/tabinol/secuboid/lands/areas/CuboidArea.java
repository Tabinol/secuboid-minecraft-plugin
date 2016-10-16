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

import me.tabinol.secuboid.utilities.Calculate;

/**
 * Represents a Cuboid area type.
 */
public final class CuboidArea extends Area {
    
    /**
     * Instantiates a new cuboid area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     */
    public CuboidArea(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        super(AreaType.CUBOID, worldName, x1, y1, z1, x2, y2, z2);
    }
    
    /**
     * Sets the x1
     * @param x1 x1
     */
    public void setX1(int x1) {

        this.x1 = x1;
    }

    /**
     * Sets the x2
     * @param x2 x2
     */
    public void setX2(int x2) {

        this.x2 = x2;
    }

    /**
     * Sets the y1
     * @param y1 y1
     */
    public void setY1(int y1) {

        this.y1 = y1;
    }

    /**
     * Sets the y2
     * @param y2 y2
     */
    public void setY2(int y2) {

        this.y2 = y2;
    }

    /**
     * Sets the z1
     * @param z1 z1
     */
    public void setZ1(int z1) {

        this.z1 = z1;
    }

    /**
     * Sets the z2
     * @param z2 z2
     */
    public void setZ2(int z2) {

        this.z2 = z2;
    }

    /**
     * Copy of.
     *
     * @return the cuboid area
     */
    public CuboidArea copyOf() {

        return new CuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }
    
    /**
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    public long getVolume() {

        return (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1);
    }

    public boolean isLocationInside(String worldName, int x, int y, int z) {

        return worldName.equals(this.worldName)
                && Calculate.isInInterval(x, x1, x2)
                && Calculate.isInInterval(y, y1, y2)
                && Calculate.isInInterval(z, z1, z2);
    }
}
