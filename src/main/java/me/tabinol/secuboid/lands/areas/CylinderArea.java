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
 * Represents a cylinder area.
 */
public final class CylinderArea implements Area {

    private final AreaCommon areaCommon;
    private double rX;
    private double rZ;
    private double originH;
    private double originK;

    /**
     * Instantiates a new cylinder area.
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
    public CylinderArea(final boolean isApproved, final String worldName, final int x1, final int y1, final int z1,
            final int x2, final int y2, final int z2) {

        areaCommon = new AreaCommon(this, isApproved, worldName, x1, y1, z1, x2, y2, z2);
        updatePos();
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
        updatePos();
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
        updatePos();
    }

    /**
     * Sets the x2. Do not use if the area is already in a land.
     *
     * @param x2 x2
     */
    @Override
    public void setX2(final int x2) {
        areaCommon.setX2(x2);
        updatePos();
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
        updatePos();
    }

    /**
     * Gets the rx.
     *
     * @return the rx
     */
    public double getRX() {

        return rX;
    }

    /**
     * Gets the rz.
     *
     * @return the rz
     */
    public double getRZ() {

        return rZ;
    }

    /**
     * Gets the origin H.
     *
     * @return the origin H
     */
    public double getOriginH() {
        return originH;
    }

    /**
     * Gets the origin K.
     *
     * @return the origin K
     */
    public double getOriginK() {
        return originK;
    }

    private void updatePos() {
        // Use "this", x2 must be greater of x1, etc.
        rX = (double) (getX2() - getX1()) / 2;
        rZ = (double) (getZ2() - getZ1()) / 2;
        originH = getX1() + rX;
        originK = getZ1() + rZ;
    }

    /**
     * Gets the z positive position from x.
     *
     * @param x the x
     * @return the position
     */
    public int getZPosFromX(final int x) {
        return (int) Math.round(originK + (rZ * Math.sqrt((rX + x - originH) * (rX - x + originH))) / rX);
    }

    /**
     * Gets the z negative position from x.
     *
     * @param x the x
     * @return the position
     */
    public int getZNegFromX(final int x) {
        return (int) Math.round(originK - (rZ * Math.sqrt((rX + x - originH) * (rX - x + originH))) / rX);
    }

    /**
     * Gets the x positive position from z.
     *
     * @param z the z
     * @return the position
     */
    public int getXPosFromZ(final int z) {
        return (int) Math.round(originH + (rX * Math.sqrt((rZ + z - originK) * (rZ - z + originK))) / rZ);
    }

    /**
     * Gets the x negative position from z.
     *
     * @param z the z
     * @return the position
     */
    public int getXNegFromZ(final int z) {
        return (int) Math.round(originH - (rX * Math.sqrt((rZ + z - originK) * (rZ - z + originK))) / rZ);
    }

    @Override
    public long getArea() {
        return Math.round(rX * rZ * Math.PI);
    }

    @Override
    public long getVolume() {
        return Math.round(rX * rZ * Math.PI * (getY2() - getY1() + 1));
    }

    @Override
    public boolean isLocationInside(final String worldName, final int x, final int z) {
        return getWorldName().equals(worldName) && ((Math.pow((x - originH), 2) / Math.pow(rX, 2))
                + (Math.pow((z - originK), 2) / Math.pow(rZ, 2))) < 1;
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
        return AreaType.CYLINDER + ":" + areaCommon.isApproved() + ":" + areaCommon.getWorldName() + ":" + getX1() + ":"
                + getY1() + ":" + getZ1() + ":" + getX2() + ":" + getY2() + ":" + getZ2();
    }

    @Override
    public String getPrint() {
        return AreaType.CYLINDER.toString().substring(0, 3).toLowerCase() + ":(" + getX1() + ", " + getY1() + ", "
                + getZ1() + ")-(" + getX2() + ", " + getY2() + ", " + getZ2() + ")";
    }

    @Override
    public AreaType getAreaType() {
        return AreaType.CYLINDER;
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
        return new CylinderArea(areaCommon.isApproved(), getWorldName(), getX1(), getY1(), getZ1(), getX2(), getY2(),
                getZ2());
    }
}
