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

/**
 * Represents a 3D point
 *
 * @author tabinol
 */
public class Point {

    private final int x;
    private final int y;
    private final int z;

    /**
     * Create a point.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public Point(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * Gets the x.
     *
     * @return the x
     */
    public int getX() {
	return x;
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    public int getY() {
	return y;
    }

    /**
     * Gets the z.
     *
     * @return the z
     */
    public int getZ() {
	return z;
    }
}
