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
import me.tabinol.secuboid.utilities.LocalMath;
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
     * @param y1           the y1
     * @param y2           the y2
     * @param regionMatrix the region matrix (can be null)
     */

    public RoadArea(String worldName, int y1, int y2, RegionMatrix regionMatrix) {

        // We need to set x, y and z after the new instance to have a reversed order
        areaCommon = new AreaCommon(this, worldName, 0, y1, 0, 0, y2, 0);
        areaCommon.setX1(Integer.MAX_VALUE);
        areaCommon.setZ1(Integer.MAX_VALUE);
        areaCommon.setX2(Integer.MIN_VALUE);
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
     * Sets the y1. Do not use if the area is already in a land.
     *
     * @param y1 y1
     */
    public void setY1(int y1) {
        areaCommon.setY1(y1);
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
     * Adds point from location. Y is ignored. Do not use if the area is already in a land.
     *
     * @param location the location
     */
    public void add(Location location) {
        add(location.getBlockX(), location.getBlockZ());
    }

    /**
     * Adds point from x and z. Do not use if the area is already in a land.
     *
     * @param x the x
     * @param z the z
     */
    public void add(int x, int z) {
        regionMatrix.addPoint(x, z);
    }

    /**
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    @Override
    public long getVolume() {
        return regionMatrix.countPoints() * (getY2() - getY1() + 1);
    }

    @Override
    public boolean isLocationInside(String worldName, int x, int y, int z) {
        return worldName.equals(areaCommon.getWorldName())
                && LocalMath.isInInterval(y, getY1(), getY2())
                && regionMatrix.getPoint(x, z);
    }

    @Override
    public boolean isLocationInside(Location loc) {
        return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public String toFileFormat() {
        return AreaType.ROAD + ":" + areaCommon.getWorldName() + ":" + getY1() + ":" + getY2() + regionMatrix.toFileFormat();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {
        return AreaType.ROAD.toString().substring(0, 3).toLowerCase()
                + ":(" + getX1() + ", " + getY1() + ", " + getZ1() + ")-("
                + getX2() + ", " + getY2() + ", " + getZ2() + ")";
    }

    @Override
    public AreaType getAreaType() {
        return AreaType.ROAD;
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
        return new RoadArea(getWorldName(), getY1(), getY2(), regionMatrix.copyOf());
    }
}
