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

import org.bukkit.Location;
import org.bukkit.World;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.LocalMath;

/**
 * Represents a cuboid area.
 */
public final class CuboidArea implements Area {

    private final AreaCommon areaCommon;

    /**
     * Instantiates a new cuboid area.
     *
     * @param isApproved is this land is approved or in approve list
     * @param worldName  the world name
     * @param x1         the x1
     * @param y1         the y1
     * @param z1         the z1
     * @param x2         the x2
     * @param y2         the y2
     * @param z2         the z2
     */
    public CuboidArea(final boolean isApproved, final String worldName, final int x1, final int y1, final int z1,
            final int x2, final int y2, final int z2) {

        areaCommon = new AreaCommon(this, isApproved, worldName, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public boolean isApproved() {
        return areaCommon.isApproved();
    }

    @Override
    public void setApproved() {
        areaCommon.setApproved();
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
    @Override
    public void setX1(final int x1) {
        areaCommon.setX1(x1);
    }

    /**
     * Sets the y1. Do not use if the area is already in a land.
     *
     * @param y1 y1
     */
    @Override
    public void setY1(final int y1) {
        areaCommon.setY1(y1);
    }

    /**
     * Sets the z1. Do not use if the area is already in a land.
     *
     * @param z1 z1
     */
    @Override
    public void setZ1(final int z1) {
        areaCommon.setZ1(z1);
    }

    /**
     * Sets the x2. Do not use if the area is already in a land.
     *
     * @param x2 x2
     */
    @Override
    public void setX2(final int x2) {
        areaCommon.setX2(x2);
    }

    /**
     * Sets the y2. Do not use if the area is already in a land.
     *
     * @param y2 y2
     */
    @Override
    public void setY2(final int y2) {
        areaCommon.setY2(y2);
    }

    /**
     * Sets the z2. Do not use if the area is already in a land.
     *
     * @param z2 z2
     */
    @Override
    public void setZ2(final int z2) {
        areaCommon.setZ2(z2);
    }

    @Override
    public long getArea() {
        return (getX2() - getX1() + 1l) * (getZ2() - getZ1() + 1l);
    }

    @Override
    public long getVolume() {
        return getArea() * (getY2() - getY1() + 1);
    }

    @Override
    public boolean isLocationInside(final String worldName, final int x, final int z) {
        return worldName.equals(areaCommon.getWorldName()) && LocalMath.isInInterval(x, getX1(), getX2())
                && LocalMath.isInInterval(z, getZ1(), getZ2());
    }

    @Override
    public boolean isLocationInside(final String worldName, final int x, final int y, final int z) {
        return isLocationInside(worldName, x, z) && LocalMath.isInInterval(y, getY1(), getY2());
    }

    @Override
    public boolean isLocationInside(final Location loc) {
        return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public boolean isLocationInsideSquare(final int x, final int z) {
        return areaCommon.isLocationInsideSquare(x, z);
    }

    @Override
    public String toFileFormat() {
        return AreaType.CUBOID + ":" + areaCommon.isApproved() + ":" + areaCommon.getWorldName() + ":" + getX1() + ":"
                + getY1() + ":" + getZ1() + ":" + getX2() + ":" + getY2() + ":" + getZ2();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {
        return AreaType.CUBOID.toString().substring(0, 3).toLowerCase() + ":(" + getX1() + ", " + getY1() + ", "
                + getZ1() + ")-(" + getX2() + ", " + getY2() + ", " + getZ2() + ")";
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
    public void setLand(final Land land) {
        areaCommon.setLand(land);
    }

    @Override
    public Land getLand() {
        return areaCommon.getLand();
    }

    @Override
    public String getWorldName() {
        return areaCommon.getWorldName();
    }

    @Override
    public World getWorld() {
        return areaCommon.getWorld();
    }

    @Override
    public int compareTo(final Area t) {
        return areaCommon.compareToArea(t);
    }

    @Override
    public Area copyOf() {
        return new CuboidArea(areaCommon.isApproved(), getWorldName(), getX1(), getY1(), getZ1(), getX2(), getY2(),
                getZ2());
    }
}
