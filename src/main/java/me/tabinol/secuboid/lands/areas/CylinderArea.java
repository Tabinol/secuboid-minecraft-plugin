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
import me.tabinol.secuboidapi.lands.areas.ApiAreaType;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.areas.ApiCylinderArea;
import org.bukkit.Location;

/**
 * Represents a cylinder area.
 */
public class CylinderArea extends Area implements ApiCylinderArea {

    private final int rX;
    private final int rZ;
    private final int originH;
    private final int originK;


    /**
     * Instantiates a new cylinder area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     */
    public CylinderArea(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        super(ApiAreaType.CYLINDER, worldName, x1, y1, z1, x2, y2, z2);
        rX = this.x2 - this.x1 + 1;
        rZ = this.z2 - this.z1 + 1;
        originH = this.x1 + (rX / 2);
        originK = this.z1 + (rZ / 2);
    }

    /**
     * Copy of.
     *
     * @return the cylinder area
     */
    public CylinderArea copyOf() {

        return new CylinderArea(worldName, x1, y1, z1, x2, y2, z2);
    }

    /**
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    public long getVolume() {

        return (long) (rX * (y2 - y1 + 1) * rZ * Math.PI);
    }

    /**
     * Checks if is collision.
     *
     * @param area2 the area2
     * @return true, if is collision
     */
    public boolean isCollision(ApiCuboidArea area2) {

        // TODO collision
        return false;
    }

    /**
     * Checks if is collision with Cylinder.
     *
     * @param area2 the area2
     * @return true, if is collision
     */
    public boolean isCollision(ApiCylinderArea area2) {

        // TODO collision
        return false;
    }

    /**
     * Checks if is location inside.
     *
     * @param loc the loc
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc) {

        return loc.getWorld().getName().equals(worldName)
                && Calculate.isInInterval(loc.getBlockY(), y1, y2)
                && ((((loc.getBlockX() - originH) ^ 2) / (rX ^ 2)) + (((loc.getBlockZ() - originK) ^ 2) / (rZ ^ 2))) <= 1;
    }
}
