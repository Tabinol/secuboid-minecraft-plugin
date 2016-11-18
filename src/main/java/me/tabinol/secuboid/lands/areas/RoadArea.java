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
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a road area.
 */
public final class RoadArea implements Area {

    private final AreaCommon areaCommon;

    /**
     * points is represented: RegionX, RegionZ: CheckX, CheckZ: x, z
     */
    private final RegionMatrix regionMatrix;

    /**
     * Instantiates a new cuboid area.
     *
     * @param worldName    the world name
     * @param regionMatrix the region matrix (can be null)
     */

    public RoadArea(String worldName, RegionMatrix regionMatrix) {

        // We need to set x, y and z after the new instance to have a reversed order
        areaCommon = new AreaCommon(this, worldName, 0, 0, 0, 0, 0, 0);
        areaCommon.setX1(Integer.MAX_VALUE);
        areaCommon.setY1(Integer.MAX_VALUE);
        areaCommon.setZ1(Integer.MAX_VALUE);
        areaCommon.setX2(Integer.MIN_VALUE);
        areaCommon.setY2(Integer.MIN_VALUE);
        areaCommon.setZ2(Integer.MIN_VALUE);
        if (regionMatrix == null) {
            this.regionMatrix = new RegionMatrix();
        } else {
            this.regionMatrix = regionMatrix;
        }
    }

    /**
     * Gets the x1.
     *
     * @return the x1
     */
    @Override
    public int getX1() {
        return areaCommon.getX1();
    }

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    @Override
    public int getY1() {
        return areaCommon.getY1();
    }

    /**
     * Gets the z1.
     *
     * @return the z1
     */
    @Override
    public int getZ1() {
        return areaCommon.getZ1();
    }

    /**
     * Gets the x2.
     *
     * @return the x2
     */
    @Override
    public int getX2() {
        return areaCommon.getX2();
    }

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    @Override
    public int getY2() {
        return areaCommon.getY2();
    }

    /**
     * Gets the z2.
     *
     * @return the z2
     */
    @Override
    public int getZ2() {
        return areaCommon.getZ2();
    }

    /**
     * Sets the x1. Do not use if the area is already in a land.
     *
     * @param x1 x1
     */
    public void setX1(int x1) {
        areaCommon.setX1(x1);
    }

    /**
     * Sets the y1. Do not use if the area is already in a land.
     *
     * @param y1 y1
     */
    public void setY1(int y1) {
        areaCommon.setY1(y1);
    }

    /**
     * Sets the z1. Do not use if the area is already in a land.
     *
     * @param z1 z1
     */
    public void setZ1(int z1) {
        areaCommon.setZ1(z1);
    }

    /**
     * Sets the x2. Do not use if the area is already in a land.
     *
     * @param x2 x2
     */
    public void setX2(int x2) {
        areaCommon.setX2(x2);
    }

    /**
     * Sets the y2. Do not use if the area is already in a land.
     *
     * @param y2 y2
     */
    public void setY2(int y2) {
        areaCommon.setY2(y2);
    }

    /**
     * Sets the z2. Do not use if the area is already in a land.
     *
     * @param z2 z2
     */
    public void setZ2(int z2) {
        areaCommon.setZ2(z2);
    }

    /**
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    @Override
    public long getVolume() {
        return regionMatrix.countPoints() * (getY2() - getY1());
    }

    @Override
    public boolean isLocationInside(String worldName, int x, int y, int z) {
        return worldName.equals(areaCommon.getWorldName())
                && Calculate.isInInterval(x, getX1(), getX2())
                && Calculate.isInInterval(y, getY1(), getY2())
                && Calculate.isInInterval(z, getZ1(), getZ2());
    }

    @Override
    public boolean isLocationInside(Location loc) {
        return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public String toFileFormat() {
        return AreaType.CUBOID + ":" + areaCommon.getWorldName()
                + ":" + getX1() + ":" + getY1() + ":" + getZ1() + ":" + getX2() + ":" + getY2() + ":" + getZ2();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {
        return AreaType.CUBOID.toString().substring(0, 3).toLowerCase()
                + ":(" + getX1() + ", " + getY1() + ", " + getZ1() + ")-("
                + getX2() + ", " + getY2() + ", " + getZ2() + ")";
    }

    @Override
    public AreaType getAreaType() {
        return AreaType.CUBOID;
    }

    @Override
    public Integer getKey() {
        return areaCommon.getKey();
    }

    @Override
    public void setLand(RealLand land) {
        areaCommon.setLand(land);
    }

    @Override
    public RealLand getLand() {
        return areaCommon.getLand();
    }

    @Override
    public String getWorldName() {
        return areaCommon.getWorldName();
    }

    @Override
    public World getWord() {
        return areaCommon.getWord();
    }

    @Override
    public int compareTo(Area t) {
        return areaCommon.compareToArea(t);
    }

    @Override
    public Area copyOf() {
        return new CuboidArea(getWorldName(), getX1(), getY1(), getZ1(), getX2(), getY2(), getZ2());
    }
}