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
import java.util.Iterator;
import java.util.List;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a lines area.
 */
public final class LinesArea implements Area {

    private final AreaCommon areaCommon;
    private final int up;
    private final int down;
    private final int radius;
    private final List<Point> points;

    /**
     * a in z = mx + b (slopes)
     */
    private final List<Double> ms;

    /**
     * b in z = mx + b
     */
    private final List<Double> bs;

    /**
     * Instantiates a line area.
     *
     * @param worldName the world name
     * @param up the up distance (from the point)
     * @param down the down distance (from the point)
     * @param radius the radius distance (left-right) (from the point)
     * @param points the points (can be null for a new lines area)
     */
    public LinesArea(String worldName, int up, int down, int radius, List<Point> points) {

	// We need to set x, y and z after the new instance to have a reversed order
	areaCommon = new AreaCommon(this, worldName, 0, 0, 0, 0, 0, 0);
	areaCommon.setX1(Integer.MAX_VALUE);
	areaCommon.setY1(Integer.MAX_VALUE);
	areaCommon.setZ1(Integer.MAX_VALUE);
	areaCommon.setX2(Integer.MIN_VALUE);
	areaCommon.setY2(Integer.MIN_VALUE);
	areaCommon.setZ2(Integer.MIN_VALUE);
	this.points = new ArrayList<Point>();
	ms = new ArrayList<Double>();
	bs = new ArrayList<Double>();
	this.up = up;
	this.down = down;
	this.radius = radius;

	// Know the minimal x/z and maximal x/z
	if (points != null) {
	    for (Point point : points) {
		addPoint(point);
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

    @Override
    public String toFileFormat() {

	StringBuilder sb = new StringBuilder();
	sb.append(AreaType.LINES).append(":").append(getWorldName()).append(":")
		.append(up).append(":").append(down).append(":").append(radius);
	for (Point point : points) {
	    sb.append(":").append(point.getX()).append(":").append(point.getY()).append(":").append(point.getZ());
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
	sb.append(AreaType.LINES.toString().substring(0, 3).toLowerCase()).append(":")
		.append("u:").append(up).append(", d:").append(down).append(", r:").append(radius);
	for (Point point : points) {
	    sb.append(", (").append(point.getX()).append(", ").append(point.getY()).append(", ").append(point.getZ()).append(")");
	}

	return sb.toString();
    }

    /**
     * Adds a point to the multiple lines. Do not use if this area is already in a land.
     *
     * @param point the point
     */
    public void addPoint(Point point) {

	points.add(point);
	if (points.size() > 1) {
	    Point previous = points.get(points.size() - 2);

	    // slope and prevents infinite
	    double m;
	    double b;
	    if (point.getX() == previous.getX()) {
		m = Double.MAX_VALUE;
		b = point.getX();
	    } else {
		m = (point.getZ() - previous.getZ()) / (point.getX() - previous.getX());
		b = point.getZ() - m * point.getX();
	    }
	    ms.add(m);
	    bs.add(b);
	}
	updateAreaCommon(point);
    }

    private void updateAreaCommon(Point point) {

	if (point.getX() - radius < areaCommon.getX1()) {
	    areaCommon.setX1(point.getX() - radius);
	}
	if (point.getX() + radius > areaCommon.getX2()) {
	    areaCommon.setX2(point.getX() + radius);
	}
	if (point.getY() - down < areaCommon.getY1()) {
	    areaCommon.setY1(point.getY() - down);
	}
	if (point.getY() + up > areaCommon.getY2()) {
	    areaCommon.setY2(point.getY() + up);
	}
	if (point.getZ() - radius < areaCommon.getZ1()) {
	    areaCommon.setZ1(point.getZ() - radius);
	}
	if (point.getZ() + radius > areaCommon.getZ2()) {
	    areaCommon.setZ2(point.getZ() + radius);
	}
    }

    /**
     * Gets all Points.
     *
     * @return a collection of point
     */
    public Collection<Point> getPoints() {

	return Collections.unmodifiableCollection(points);
    }

    @Override
    public long getVolume() {
	// Points by points is very slow solution but for now, I don't have an other solution
	int volume = 0;
	for (int x = areaCommon.getX1(); x <= areaCommon.getX2(); x++) {
	    for (int y = areaCommon.getY1(); y <= areaCommon.getY2(); y++) {
		for (int z = areaCommon.getZ1(); z <= areaCommon.getZ2(); z++) {
		    if (isLocationInside(areaCommon.getWorldName(), x, y, z)) {
			volume++;
		    }
		}
	    }
	}
	return volume;
    }

    @Override
    public boolean isLocationInside(String worldName, int x, int y, int z) {

	if (worldName.equals(worldName)) {
	    Iterator<Point> iterator = points.iterator();
	    Point previous = iterator.next();
	    int i = 0;
	    while (iterator.hasNext()) {
		Point point = iterator.next();
		double m = ms.get(i);
		double b = bs.get(i);

		// first, check for y
		int yl = Calculate.lowerInt(point.getY(), previous.getY());
		int yh = Calculate.greaterInt(point.getY(), previous.getY());
		if (y >= yl - down && y <= yh + up) {
		    // check in z
		    int zMinLine;
		    int zMaxLine;
		    // infinite slope
		    if (m == Double.MAX_VALUE) {
			zMinLine = Calculate.lowerInt(point.getZ(), previous.getZ()) - radius;
			zMaxLine = Calculate.greaterInt(point.getZ(), previous.getZ()) + radius;
		    } else {
			int zLine = (int) (m * x + b);
			zMinLine = zLine - radius;
			zMaxLine = zLine + radius;
		    }
		    if (z >= zMinLine && z <= zMaxLine) {
			// check in x
			int xMinLine;
			int xMaxLine;

			if (m == 0) {
			    // zero slope
			    xMinLine = Calculate.lowerInt(point.getX(), previous.getX()) - radius;
			    xMaxLine = Calculate.greaterInt(point.getX(), previous.getX()) + radius;
			} else if (m == Double.MAX_VALUE) {
			    // infinite slope
			    xMinLine = (int) b - radius;
			    xMaxLine = (int) b + radius;
			} else {
			    // normal slope
			    int xLine = (int) ((z - b) / m);
			    xMinLine = xLine - radius;
			    xMaxLine = xLine + radius;
			}
			if (x >= xMinLine && x <= xMaxLine) {
			    return true;
			}
		    }
		}
		i++;
		previous = point;
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

	final List<Point> newPoints = new ArrayList<Point>();

	for (Point point : points) {
	    newPoints.add(point);
	}
	return new LinesArea(getWorldName(), up, down, radius, newPoints);
    }
}
