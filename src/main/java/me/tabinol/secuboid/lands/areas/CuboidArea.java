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

import java.util.Collection;
import java.util.HashSet;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.Calculate;
import me.tabinol.secuboidapi.lands.areas.ApiAreaType;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;

import me.tabinol.secuboidapi.lands.areas.ApiCylinderArea;
import org.bukkit.Location;
import org.bukkit.World;


/**
 * Represents a Cuboid area type.
 */
public class CuboidArea extends Area implements ApiCuboidArea {

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

        super(ApiAreaType.CUBOID, worldName, x1, y1, z1, x2, y2, z2);
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

    /**
     * Checks if is collision.
     *
     * @param area2 the area2
     * @return true, if is collision
     */
    public boolean isCollision(ApiCuboidArea area2) {

        return (worldName.equals(area2.getWorldName())
                && (Calculate.isInInterval(x1, area2.getX1(), area2.getX2())
                || Calculate.isInInterval(area2.getX1(), x1, x2)))
                && ((Calculate.isInInterval(y1, area2.getY1(), area2.getY2())
                || Calculate.isInInterval(area2.getY1(), y1, y2)))
                && ((Calculate.isInInterval(z1, area2.getZ1(), area2.getZ2())
                || Calculate.isInInterval(area2.getZ1(), z1, z2)));
    }

    /**
     * Checks if is collision with Cylinder.
     *
     * @param area2 the area2
     * @return true, if is collision
     */
    public boolean isCollision(ApiCylinderArea area2) {

        return area2.isCollision(this);
    }

    /**
     * Checks if is location inside.
     *
     * @param loc the loc
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc) {

        return loc.getWorld().getName().equals(worldName)
                && Calculate.isInInterval(loc.getBlockX(), x1, x2)
                && Calculate.isInInterval(loc.getBlockY(), y1, y2)
                && Calculate.isInInterval(loc.getBlockZ(), z1, z2);
    }
    
    /**
     * Check if there is a collision and create an area of the collision.
     * @param area2 The second area to compare
     * @return the CuboidArea collision or null if there is no collision
     */
    public CuboidArea getCollisionArea(ApiCuboidArea area2) {
        
        // Return null if the world is not the same
        if (!worldName.equals(area2.getWorldName())) {
            return null;
        }
        
        // -1 before, 0 inside, +1 after
        int x1pos = Calculate.comparePosition(area2.getX1(), x1, x2);
        int y1pos = Calculate.comparePosition(area2.getY1(), y1, y2);
        int z1pos = Calculate.comparePosition(area2.getZ1(), z1, z2);
        int x2pos = Calculate.comparePosition(area2.getX2(), x1, x2);
        int y2pos = Calculate.comparePosition(area2.getX2(), y1, y2);
        int z2pos = Calculate.comparePosition(area2.getZ2(), z1, z2);
        
        // first check if both points are before or after
        if ((x1pos == -1 && x2pos == -1) || (x1pos == 1 && x2pos == 1)) {
            return null;
        }
        if ((y1pos == -1 && y2pos == -1) || (y1pos == 1 && y2pos == 1)) {
            return null;
        }
        if ((z1pos == -1 && z2pos == -1) || (z1pos == 1 && z2pos == 1)) {
            return null;
        }
        
        // At this point, there is a collision
        
        int cx1;
        int cx2;
        int cy1;
        int cy2;
        int cz1;
        int cz2;

        // Create points
        if(x1pos == -1) {
            cx1 = x1;
        } else {
            cx1 = area2.getX1();
        }
        if(y1pos == -1) {
            cy1 = y1;
        } else {
            cy1 = area2.getY1();
        }
        if(z1pos == -1) {
            cz1 = z1;
        } else {
            cz1 = area2.getZ1();
        }
        if(x2pos == 1) {
            cx2 = x2;
        } else {
            cx2 = area2.getX2();
        }
        if(y2pos == 1) {
            cy2 = y2;
        } else {
            cy2 = area2.getY2();
        }
        if(z2pos == 1) {
            cz2 = z2;
        } else {
            cz2 = area2.getZ2();
        }
        
        // Return collision area
        return new CuboidArea(worldName, cx1, cy1, cz1, cx2, cy2, cz2);
    }
    
    /**
     * Create a collection of outside areas. DO NOT USE TO GET THE AREA PRICE
     * @param area2 Area to compare
     * @return A collection of outside areas
     */
    public Collection<ApiCuboidArea>
        getOutside(ApiCuboidArea area2) {

        HashSet<ApiCuboidArea> areaList = new HashSet<ApiCuboidArea>();

        if (!worldName.equals(area2.getWorldName())) {
            areaList.add(area2);
            return areaList;
        }

        Integer ax1 = null;
        Integer ax2 = null;
        Integer bx1 = null;
        Integer bx2 = null;
        Integer ay1 = null;
        Integer ay2 = null;
        Integer by1 = null;
        Integer by2 = null;
        Integer az1 = null;
        Integer az2 = null;
        Integer bz1 = null;
        Integer bz2 = null;

        // -1 before, 0 inside, +1 after
        int x1pos = Calculate.comparePosition(area2.getX1(), x1, x2);
        int y1pos = Calculate.comparePosition(area2.getY1(), y1, y2);
        int z1pos = Calculate.comparePosition(area2.getZ1(), z1, z2);
        int x2pos = Calculate.comparePosition(area2.getX2(), x1, x2);
        int y2pos = Calculate.comparePosition(area2.getY2(), y1, y2);
        int z2pos = Calculate.comparePosition(area2.getZ2(), z1, z2);

        // first check if both points are before or after
        if ((x1pos == -1 && x2pos == -1) || (x1pos == 1 && x2pos == 1)) {
            areaList.add(area2);
            return areaList;
        }
        if ((y1pos == -1 && y2pos == -1) || (y1pos == 1 && y2pos == 1)) {
            areaList.add(area2);
            return areaList;
        }
        if ((z1pos == -1 && z2pos == -1) || (z1pos == 1 && z2pos == 1)) {
            areaList.add(area2);
            return areaList;
        }
        
        // Check positions before
        if (x1pos == -1) {
            ax1 = area2.getX1();
            ax2 = x1;
        }
        if (x2pos == 1) {
            bx1 = x2;
            bx2 = area2.getX2();
        }
        if (y1pos == -1) {
            ay1 = area2.getY1();
            ay2 = y1;
        }
        if (y2pos == 1) {
            by1 = y2;
            by2 = area2.getY2();
        }
        if (z1pos == -1) {
            az1 = area2.getZ1();
            az2 = z1;
        }
        if (z2pos == 1) {
            bz1 = z2;
            bz2 = area2.getZ2();
        }

        // Create areas
        if (ax1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1, ay1 != null ? ay1 : area2.getY2(), az1 != null ? az1 : area2.getZ2(),
                    ax2, ay2 != null ? ay2 : area2.getY1(), az2 != null ? az2 : area2.getZ1()));
        }
        if (bx1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1, by1 != null ? by1 : area2.getY2(), bz1 != null ? bz1 : area2.getZ2(),
                    bx2, by2 != null ? by2 : area2.getY1(), bz2 != null ? bz2 : area2.getZ1()));
        }
        if (ay1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1 != null ? ax1 : area2.getX2(), ay1, az1 != null ? az1 : area2.getZ2(),
                    ax2 != null ? ax2 : area2.getX1(), ay2, az2 != null ? az2 : area2.getZ1()));
        }
        if (by1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1 != null ? bx1 : area2.getX2(), by1, bz1 != null ? bz1 : area2.getZ2(),
                    bx2 != null ? bx2 : area2.getX1(), by2, bz2 != null ? bz2 : area2.getZ1()));
        }
        if (az1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1 != null ? ax1 : area2.getX2(), ay1 != null ? ay1 : area2.getY2(), az1,
                    ax2 != null ? ax2 : area2.getX1(), ay2 != null ? ay2 : area2.getY1(), az2));
        }
        if (bz1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1 != null ? bx1 : area2.getX2(), by1 != null ? by1 : area2.getY2(), bz1,
                    bx2 != null ? bx2 : area2.getX1(), by2 != null ? by2 : area2.getY1(), bz2));
        }

        return areaList;
    }
}
