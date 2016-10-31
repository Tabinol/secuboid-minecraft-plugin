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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a lines area.
 */
public final class LinesArea implements Area {

    private final AreaCommon areaCommon;
    private final List<LineLine> lines;

    /**
     * Instantiates a line area.
     *
     * @param worldName the world name
     * @param lines the lines
     */
    public LinesArea(String worldName, Collection<LineLine> lines) {

	areaCommon = new AreaCommon(this, worldName, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
		Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	this.lines = new ArrayList<LineLine>();

	// Know the minimal x/z and maximal x/z
	if (lines != null) {
	    for (LineLine line : lines) {
		addLine(line);
	    }
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

    @Override
    public String toFileFormat() {

	StringBuilder sb = new StringBuilder();
	sb.append(AreaType.LINES).append(":").append(getWorldName());
	boolean isFirst = true;
	for (LineLine line : getLines()) {
	    sb.append(":").append(line.toString(isFirst));
	    isFirst = false;
	}

	return sb.toString();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {

	StringBuilder sb = new StringBuilder();
	sb.append(AreaType.LINES.toString().substring(0, 3).toLowerCase()).append(":");
	boolean isFirst = true;
	for (LineLine line : getLines()) {
	    if (!isFirst) {
		sb.append(", ");
	    }
	    sb.append(line.getPrint(isFirst));
	    isFirst = false;
	}

	return sb.toString();
    }

    /**
     *
     * @param line
     */
    public void addLine(LineLine line) {

	// Modify the previous line x2/y2/z2
	if (!lines.isEmpty()) {
	    lines.get(lines.size() - 1).resolveIntersection(line);
	}

	this.lines.add(line);
	setX1(Calculate.lowerInt(Calculate.lowerInt(line.getLeftX1(), line.getLeftX2()),
		Calculate.lowerInt(line.getRightX1(), line.getRightX2())));
	setY1(line.getY1());
	setZ1(Calculate.lowerInt(Calculate.lowerInt(line.getLeftZ1(), line.getLeftZ2()),
		Calculate.lowerInt(line.getRightZ1(), line.getRightZ2())));
	setX2(Calculate.greaterInt(Calculate.greaterInt(line.getLeftX1(), line.getLeftX2()),
		Calculate.greaterInt(line.getRightX1(), line.getRightX2())));
	setY2(line.gety2());
	setZ2(Calculate.greaterInt(Calculate.greaterInt(line.getLeftZ1(), line.getLeftZ2()),
		Calculate.greaterInt(line.getRightZ1(), line.getRightZ2())));
    }

    /**
     * Gets all lines.
     *
     * @return a collection of line
     */
    public Collection<LineLine> getLines() {

	return Collections.unmodifiableCollection(lines);
    }

    @Override
    public long getVolume() {

	long volume = 0;

	for (LineLine line : lines) {
	    volume += ((LineLine) line).getVolume();
	}

	return volume;
    }

    @Override
    public boolean isLocationInside(String worldName, int x, int y, int z) {

	if (worldName.equals(worldName)) {
	    for (LineLine line : lines) {
		if (((LineLine) line).isLocationInside(x, y, z)) {
		    return true;
		}
	    }
	}

	return false;
    }

    @Override
    public boolean isLocationInside(Location loc) {
	return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public AreaType getAreaType() {
	return AreaType.LINES;
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

	final List<LineLine> newLines = new ArrayList<LineLine>();

	for (LineLine line : lines) {
	    newLines.add(line);
	}
	return new LinesArea(getWorldName(), newLines);
    }
}
