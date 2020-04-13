/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approvable;
import me.tabinol.secuboid.storage.SavableParameter;

/**
 * Represents a area of any type (abstract).
 */
public interface Area extends Comparable<Area>, Approvable, SavableParameter {

    /**
     * Gets the Area from string file format.
     *
     * @param str the str
     * @return the from string
     */
    static Area getFromFileFormat(final String str) {
        final String[] multiStr = str.split(":");
        final String areaTypeStr = multiStr[0];

        // Detect if it contains the "approved" parameter (or legacy)
        // parseBoolean method returns "false" if the value is not boolean
        final boolean isApproved;
        final int firstIdx;
        switch (multiStr[1]) {
        case "true":
            isApproved = true;
            firstIdx = 2;
            break;
        case "false":
            isApproved = false;
            firstIdx = 2;
            break;
        default:
            // Legacy
            isApproved = true;
            firstIdx = 1;
        }

        // Create cuboid area
        if (areaTypeStr.equals(AreaType.CUBOID.toString())) {
            return new CuboidArea(isApproved, multiStr[firstIdx], Integer.parseInt(multiStr[firstIdx + 1]),
                    Integer.parseInt(multiStr[firstIdx + 2]), Integer.parseInt(multiStr[firstIdx + 3]),
                    Integer.parseInt(multiStr[firstIdx + 4]), Integer.parseInt(multiStr[firstIdx + 5]),
                    Integer.parseInt(multiStr[firstIdx + 6]));
        }

        // Create cylinder area
        if (areaTypeStr.equals(AreaType.CYLINDER.toString())) {
            return new CylinderArea(isApproved, multiStr[firstIdx], Integer.parseInt(multiStr[firstIdx + 1]),
                    Integer.parseInt(multiStr[firstIdx + 2]), Integer.parseInt(multiStr[firstIdx + 3]),
                    Integer.parseInt(multiStr[firstIdx + 4]), Integer.parseInt(multiStr[firstIdx + 5]),
                    Integer.parseInt(multiStr[firstIdx + 6]));
        }
        // Create road area
        if (areaTypeStr.equals(AreaType.ROAD.toString())) {
            final Map<Integer, Map<Integer, ChunkMatrix>> points = new HashMap<>();
            for (int i = firstIdx + 3; i < multiStr.length; i += 3) {
                final int regionX = Integer.parseInt(multiStr[i]);
                final int regionZ = Integer.parseInt(multiStr[i + 1]);
                final String hex = multiStr[i + 2];
                final short[] matrix = new short[16];
                for (int j = 0; j < 16; j++) {
                    matrix[j] = (short) (Integer.parseInt(hex.substring(j * 4, (j * 4) + 4), 16) & 0xffff);
                }
                final ChunkMatrix chunkMatrix = new ChunkMatrix(matrix);
                Map<Integer, ChunkMatrix> pointX = points.get(regionX);
                if (pointX == null) {
                    pointX = new HashMap<>();
                    points.put(regionX, pointX);
                }
                pointX.put(regionZ, chunkMatrix);
            }
            return new RoadArea(isApproved, multiStr[firstIdx], Integer.parseInt(multiStr[firstIdx + 1]),
                    Integer.parseInt(multiStr[firstIdx + 2]), new RegionMatrix(points));
        }

        // Create CuboidArea (old version)
        return new CuboidArea(isApproved, multiStr[0], Integer.parseInt(multiStr[1]), Integer.parseInt(multiStr[2]),
                Integer.parseInt(multiStr[3]), Integer.parseInt(multiStr[4]), Integer.parseInt(multiStr[5]),
                Integer.parseInt(multiStr[6]));
    }

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
    void setLand(Land land);

    /**
     * Gets the land.
     *
     * @return the land
     */
    Land getLand();

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    String getWorldName();

    /**
     * Gets the world.
     *
     * @return the world
     */
    World getWorld();

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
     * Sets the x1. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param x1 x1
     */
    void setX1(int x1);

    /**
     * Sets the x2. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param x2 x2
     */
    void setX2(int x2);

    /**
     * Sets the y1. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param y1 y1
     */
    void setY1(int y1);

    /**
     * Sets the y2. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param y2 y2
     */
    void setY2(int y2);

    /**
     * Sets the z1. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param z1 z1
     */
    void setZ1(int z1);

    /**
     * Sets the z2. Do not use if the area is already in a land. Normally, it us for
     * internal use. For roads, it changes only the limit to start and stop looking
     * the matrix.
     *
     * @param z2 z2
     */
    void setZ2(int z2);

    /**
     * Gets the area (surface).
     *
     * @return the area
     */
    long getArea();

    /**
     * Gets the volume.
     *
     * @return the volume
     */
    long getVolume();

    /**
     * Gets if the location is inside the area. This method ignore y value.
     *
     * @param worldName the world name
     * @param x         the x
     * @param z         the z
     * @return if true or false
     */
    boolean isLocationInside(String worldName, int x, int z);

    /**
     * Gets if the location is inside the area.
     *
     * @param worldName the world name
     * @param x         the x
     * @param y         the y
     * @param z         the z
     * @return if true or false
     */
    boolean isLocationInside(String worldName, int x, int y, int z);

    /**
     * Gets if the location is inside the area.
     *
     * @param loc the location
     * @return if true or false
     */
    boolean isLocationInside(Location loc);

    /**
     * Gets if the location is in the square limit of the land. This method ignore
     * the world and the y values. Use isLocationInside methods if you want to check
     * an exact location.
     *
     * @param x the x
     * @param z the z
     * @return if true or false
     */
    boolean isLocationInsideSquare(int x, int z);

    /**
     * Gets an exact copy of this area.
     *
     * @return an area copy
     */
    Area copyOf();
}
