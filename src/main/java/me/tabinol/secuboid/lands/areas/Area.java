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

import java.util.ArrayList;
import java.util.List;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a area of any type (abstract).
 */
public abstract class Area implements Comparable<Area> {

    /** The world name. */
    protected final String worldName;

    /** The values. */
    protected int x1, y1, z1, x2, y2, z2;

    /** The land. */
    protected Land land = null;

    /** The area type. */
    protected final AreaType areaType;

    /**
     * Instantiates a new area.
     *
     * @param areaType the area type
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     */
    Area(AreaType areaType, String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        this.areaType = areaType;
        this.worldName = worldName;
        this.x1 = Calculate.lowerInt(x1, x2);
        this.x2 = Calculate.greaterInt(x1, x2);
        this.y1 = Calculate.lowerInt(y1, y2);
        this.y2 = Calculate.greaterInt(y1, y2);
        this.z1 = Calculate.lowerInt(z1, z2);
        this.z2 = Calculate.greaterInt(z1, z2);
    }
    
    /**
     * Equals.
     *
     * @param area2 the area2
     * @return true, if successful
     */
    public boolean equals(CuboidArea area2) {

        return areaType == areaType && worldName.equals(area2.getWorldName())
                && x1 == area2.getX1() && y1 == area2.getY1() && z1 == area2.getZ1()
                && x2 == area2.getX2() && y2 == area2.getY2() && z2 == area2.getZ2();
    }

    @Override
    public int compareTo(Area t) {

        int worldCompare = worldName.compareTo(t.worldName);
        if (worldCompare != 0) {
            return worldCompare;
        }
        if (x1 < t.x1) {
            return -1;
        }
        if (x1 > t.x1) {
            return 1;
        }
        if (z1 < t.z1) {
            return -1;
        }
        if (z1 > t.z1) {
            return 1;
        }
        if (y1 < t.y1) {
            return -1;
        }
        if (y1 > t.y1) {
            return 1;
        }
        if (x2 < t.x2) {
            return -1;
        }
        if (x2 > t.x2) {
            return 1;
        }
        if (z2 < t.z2) {
            return -1;
        }
        if (z2 > t.z2) {
            return 1;
        }
        if (y2 < t.y2) {
            return -1;
        }
        if (y2 > t.y2) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {

        return areaType + ":" + worldName + ":" + x1 + ":" + y1 + ":" + z1 + ":" + x2 + ":" + y2 + ":" + z2;
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    public String getPrint() {

        return areaType.toString().substring(0, 3).toLowerCase()
                + ":(" + x1 + ", " + y1 + ", " + z1 + ")-(" + x2 + ", " + y2 + ", " + z2 + ")";
    }

    public AreaType getAreaType() {
        
        return areaType;
    }
    
    /**
     * Gets the key.
     *
     * @return the key
     */
    public Integer getKey() {

        if (land != null) {
            return land.getAreaKey(this);
        }

        return null;
    }

    /**
     * Sets the land.
     *
     * @param land the new land
     */
    public final void setLand(Land land) {

        this.land = (Land) land;
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {

        return land;
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    public String getWorldName() {

        return worldName;
    }

    /**
     * Gets the word.
     *
     * @return the word
     */
    public World getWord() {

        return Secuboid.getThisPlugin().getServer().getWorld(worldName);
    }

    /**
     * Gets the x1.
     *
     * @return the x1
     */
    public int getX1() {

        return x1;
    }

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    public int getY1() {

        return y1;
    }

    /**
     * Gets the z1.
     *
     * @return the z1
     */
    public int getZ1() {

        return z1;
    }

    /**
     * Gets the x2.
     *
     * @return the x2
     */
    public int getX2() {

        return x2;
    }

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    public int getY2() {

        return y2;
    }

    /**
     * Gets the z2.
     *
     * @return the z2
     */
    public int getZ2() {

        return z2;
    }
    
    public abstract long getVolume();

    public abstract boolean isLocationInside(String worldName, int x, int y, int z);
    
    public boolean isLocationInside(Location loc) {

        return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Gets the from string.
     *
     * @param str the str
     * @return the from string
     */
    public static Area getFromString(String str) {

        String[] multiStr = str.split(":");

        // TODO Lines
        // Create CuboidArea
        if(multiStr[0].equals("CUBOID")) {
            return new CuboidArea(multiStr[1],
                    Integer.parseInt(multiStr[2]),
                    Integer.parseInt(multiStr[3]),
                    Integer.parseInt(multiStr[4]),
                    Integer.parseInt(multiStr[5]),
                    Integer.parseInt(multiStr[6]),
                    Integer.parseInt(multiStr[7]));
        }

        // Create CylinderArea
        if(multiStr[0].equals("CYLINDER")) {
            return new CylinderArea(multiStr[1],
                    Integer.parseInt(multiStr[2]),
                    Integer.parseInt(multiStr[3]),
                    Integer.parseInt(multiStr[4]),
                    Integer.parseInt(multiStr[5]),
                    Integer.parseInt(multiStr[6]),
                    Integer.parseInt(multiStr[7]));
        }

        // Create CylinderArea
        if(multiStr[0].equals("LINES")) {
            List<LineLine> lines = new ArrayList<LineLine>();

            // Do the first
            lines.add(new LineLine(
                    Integer.parseInt(multiStr[2]),
                    Integer.parseInt(multiStr[3]),
                    Integer.parseInt(multiStr[4]),
                    Integer.parseInt(multiStr[5]),
                    Integer.parseInt(multiStr[6]),
                    Integer.parseInt(multiStr[7]),
                    Integer.parseInt(multiStr[8]),
                    Integer.parseInt(multiStr[9]),
                    Integer.parseInt(multiStr[10]),
                    Integer.parseInt(multiStr[11])
            ));

            // Do the next lines (if exist)
            if(multiStr.length > 12) {
                for (int t = 12; t < multiStr.length; t += 10) {
                    lines.add(new LineLine(
                            Integer.parseInt(multiStr[t - 7]),
                            Integer.parseInt(multiStr[t - 6]),
                            Integer.parseInt(multiStr[t - 5]),
                            Integer.parseInt(multiStr[t]),
                            Integer.parseInt(multiStr[t + 1]),
                            Integer.parseInt(multiStr[t + 2]),
                            Integer.parseInt(multiStr[t + 3]),
                            Integer.parseInt(multiStr[t + 4]),
                            Integer.parseInt(multiStr[t + 5]),
                            Integer.parseInt(multiStr[t + 6])
                    ));
                }
            }
            return new LinesArea(multiStr[1], lines);
        }

        // Create CuboidArea (old version)
        return new CuboidArea(multiStr[0],
                Integer.parseInt(multiStr[1]),
                Integer.parseInt(multiStr[2]),
                Integer.parseInt(multiStr[3]),
                Integer.parseInt(multiStr[4]),
                Integer.parseInt(multiStr[5]),
                Integer.parseInt(multiStr[6]));
    }
    
    public abstract Area copyOf();
}
