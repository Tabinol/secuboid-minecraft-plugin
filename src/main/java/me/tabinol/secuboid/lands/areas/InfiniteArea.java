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

import me.tabinol.secuboid.lands.Land;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents an entire world area.
 */
public class InfiniteArea implements Area {

    private final AreaCommon areaCommon;

    /**
     * Instantiates an infinite area.
     *
     * @param worldName the world name
     */
    public InfiniteArea(String worldName) {

	areaCommon = new AreaCommon(this, worldName, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
		Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
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
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    @Override
    public long getVolume() {
	return Long.MAX_VALUE;
    }

    /**
     *
     * @param worldName
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Override
    public boolean isLocationInside(String worldName, int x, int y, int z) {
	if (getWorldName() == null) {
	    return true;
	}
	return worldName.equals(areaCommon.getWorldName());
    }

    @Override
    public boolean isLocationInside(Location loc) {
	return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public String toFileFormat() {
	return AreaType.INFINITE + ":" + areaCommon.getWorldName() == null ? null : areaCommon.getWorldName();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {
	return AreaType.INFINITE.toString().toLowerCase();
    }

    @Override
    public AreaType getAreaType() {
	return AreaType.INFINITE;
    }

    @Override
    public Integer getKey() {
	return areaCommon.getKey();
    }

    @Override
    public void setLand(Land land) {
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
    public World getWord() {
	return areaCommon.getWord();
    }

    @Override
    public int compareTo(Area t) {
	return areaCommon.compareToArea(t);
    }

    @Override
    public Area copyOf() {
	return new InfiniteArea(getWorldName());
    }
}
