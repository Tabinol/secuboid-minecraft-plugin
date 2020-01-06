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

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.LocalMath;

/**
 * Area common methodes.
 */
final class AreaCommon {

    /**
     * The area.
     */
    private final Area area;

    private boolean isApproved;

    /**
     * The world name.
     */
    private final String worldName;

    /**
     * The values.
     */
    private int x1, y1, z1, x2, y2, z2;

    /**
     * The land.
     */
    private Land land = null;

    /**
     * Instantiates a new area.
     *
     * @param area       the area
     * @param isApproved is this land is approved or in approve list
     * @param worldName  the world name
     * @param x1         the x1
     * @param y1         the y1
     * @param z1         the z1
     * @param x2         the x2
     * @param y2         the y2
     * @param z2         the z2
     */
    AreaCommon(final Area area, final boolean isApproved, final String worldName, final int x1, final int y1,
            final int z1, final int x2, final int y2, final int z2) {

        this.area = area;
        this.worldName = worldName;
        this.x1 = LocalMath.lowerInt(x1, x2);
        this.x2 = LocalMath.greaterInt(x1, x2);
        this.y1 = LocalMath.lowerInt(y1, y2);
        this.y2 = LocalMath.greaterInt(y1, y2);
        this.z1 = LocalMath.lowerInt(z1, z2);
        this.z2 = LocalMath.greaterInt(z1, z2);
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    Integer getKey() {

        if (land != null) {
            return land.getAreaKey(area);
        }

        return null;
    }

    /**
     * Sets the land.
     *
     * @param land the new land
     */
    final void setLand(final Land land) {
        this.land = land;
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    Land getLand() {
        return land;
    }

    boolean isApproved() {
        return isApproved;
    }

    void setApproved() {
        isApproved = true;
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    String getWorldName() {
        return worldName;
    }

    /**
     * Gets the word.
     *
     * @return the word
     */
    World getWord() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Gets the x1.
     *
     * @return the x1
     */
    int getX1() {
        return x1;
    }

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    int getY1() {
        return y1;
    }

    /**
     * Gets the z1.
     *
     * @return the z1
     */
    int getZ1() {
        return z1;
    }

    /**
     * Gets the x2.
     *
     * @return the x2
     */
    int getX2() {
        return x2;
    }

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    int getY2() {
        return y2;
    }

    /**
     * Gets the z2.
     *
     * @return the z2
     */
    int getZ2() {
        return z2;
    }

    /**
     * Sets the x1. Do not use if the area is already in a land.
     *
     * @param x1 x1
     */
    void setX1(final int x1) {
        this.x1 = x1;
    }

    /**
     * Sets the x2. Do not use if the area is already in a land.
     *
     * @param x2 x2
     */
    void setX2(final int x2) {
        this.x2 = x2;
    }

    /**
     * Sets the y1. Do not use if the area is already in a land.
     *
     * @param y1 y1
     */
    void setY1(final int y1) {
        this.y1 = y1;
    }

    /**
     * Sets the y2. Do not use if the area is already in a land.
     *
     * @param y2 y2
     */
    void setY2(final int y2) {
        this.y2 = y2;
    }

    /**
     * Sets the z1. Do not use if the area is already in a land.
     *
     * @param z1 z1
     */
    void setZ1(final int z1) {
        this.z1 = z1;
    }

    /**
     * Sets the z2. Do not use if the area is already in a land.
     *
     * @param z2 z2
     */
    void setZ2(final int z2) {
        this.z2 = z2;
    }

    /**
     * Gets if the location is in the square limit of the land. This method ignore
     * the world and the y values. Use isLocationInside methods if you want to check
     * an exact location.
     *
     * @param x the x
     * @param z the z
     * @return if true or false
     */
    boolean isLocationInsideSquare(final int x, final int z) {
        return LocalMath.isInInterval(x, getX1(), getX2()) && LocalMath.isInInterval(z, getZ1(), getZ2());
    }

    /**
     * Implements compareTo. This methode is not overrided because this class is not
     * public
     *
     * @param t the area
     * @return integer
     */
    int compareToArea(final Area t) {

        final int worldCompare = worldName.compareTo(t.getWorldName());
        if (worldCompare != 0) {
            return worldCompare;
        }
        if (x1 < t.getX1()) {
            return -1;
        }
        if (x1 > t.getX1()) {
            return 1;
        }
        if (z1 < t.getZ1()) {
            return -1;
        }
        if (z1 > t.getZ1()) {
            return 1;
        }
        if (y1 < t.getY1()) {
            return -1;
        }
        if (y1 > t.getY1()) {
            return 1;
        }
        if (x2 < t.getX2()) {
            return -1;
        }
        if (x2 > t.getX2()) {
            return 1;
        }
        if (z2 < t.getZ2()) {
            return -1;
        }
        if (z2 > t.getZ2()) {
            return 1;
        }
        if (y2 < t.getY2()) {
            return -1;
        }
        if (y2 > t.getY2()) {
            return 1;
        }
        return 0;
    }
}
