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
package me.tabinol.secuboid.lands.areas.lines;

import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;

/**
 * Represent a line in line area
 *
 * @author tabinol
 */
public class LineLine {

    private final int x1;
    private int leftX1;
    private int rightX1;
    private final int y1;
    private final int z1;
    private int leftZ1;
    private int rightZ1;
    private final int x2;
    private int leftX2;
    private int rightX2;
    private final int y2;
    private final int z2;
    private int leftZ2;
    private int rightZ2;
    private final double a;
    private double a1;
    private double a2;
    private final double b;
    private double b1;
    private double b2;
    private final double leftB;
    private final double rightB;
    private final int upDist;
    private final int downDist;
    private final int leftDist;
    private final int rightDist;

    /**
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param upDist
     * @param downDist
     * @param leftDist
     * @param rightDist
     */
    public LineLine(int x1, int y1, int z1, int x2, int y2, int z2,
	    int upDist, int downDist, int leftDist, int rightDist) {

	this.x1 = x1;
	this.x2 = x2;
	this.y1 = Calculate.lowerInt(y1, y2) - downDist;
	this.y2 = Calculate.greaterInt(y1, y2) + upDist;
	this.z1 = z1;
	this.z2 = z2;
	this.upDist = upDist;
	this.downDist = downDist;
	this.leftDist = leftDist;
	this.rightDist = rightDist;

	// a = slope, b = z when x = 0
	// a1/b1 represent lower line
	// a2/b2 represent upper line
	// l = left, r = right
	if (x2 - x1 == 0) {
	    a = Double.POSITIVE_INFINITY;
	} else {
	    a = (z2 - z1) / (x2 - x1);
	}
	b = z1 - (a * x1);
	a1 = -(1 / a);
	a2 = a1;
	b1 = z1 - (a1 * x1);
	b2 = z2 - (a2 * x2);

	// Calculate B of lower and upper parallel
	leftB = b - leftDist * Math.sqrt(Math.pow(a, 2) + 1);
	rightB = b + leftDist * Math.sqrt(Math.pow(a, 2) + 1);

	// Find heach points We need to calculate with double, not with integer
	double tmpLeftX1 = ((leftB - b1) / (a1 - a));
	leftX1 = (int) tmpLeftX1;
	leftZ1 = (int) ((a1 * tmpLeftX1) + b1);
	double tmpRightX1 = ((rightB - b1) / (a1 - a));
	rightX1 = (int) tmpRightX1;
	rightZ1 = (int) ((a1 * tmpRightX1) + b1);
	double tmpLeftX2 = ((leftB - b2) / (a2 - a));
	leftX2 = (int) tmpLeftX2;
	leftZ2 = (int) ((a2 * tmpLeftX2) + b2);
	double tmpRightX2 = ((rightB - b2) / (a2 - a));
	rightX2 = (int) tmpRightX2;
	rightZ2 = (int) ((a2 * tmpRightX2) + b2);

    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {

	return toString(true);
    }

    /**
     *
     * @param isFirst
     * @return
     */
    public String toString(boolean isFirst) {

	if (isFirst) {
	    return x1 + ":" + y1 + ":" + z1 + ":" + x2 + ":" + y2 + ":" + z2 + ":" + upDist + ":" + downDist + ":"
		    + leftDist + ":" + rightDist;
	} else {
	    return x2 + ":" + y2 + ":" + z2 + ":" + upDist + ":" + downDist + ":" + leftDist + ":" + rightDist;
	}
    }

    /**
     *
     * @return
     */
    public String getPrint() {

	return getPrint(true);
    }

    /**
     *
     * @param isFirst
     * @return
     */
    public String getPrint(boolean isFirst) {

	if (isFirst) {
	    return "(" + x1 + ", " + y1 + ", " + z1 + "), (" + x2 + ", " + y2 + ", " + z2
		    + "(up:" + upDist + ", down:" + downDist + ", left:" + leftDist + ", right:" + rightDist + "))";
	} else {
	    return "(" + x2 + ", " + y2 + ", " + z2
		    + "(up:" + upDist + ", down:" + downDist + ", left:" + leftDist + ", right:" + rightDist + "))";
	}
    }

    /**
     *
     * @param apiLine2
     */
    public void resolveIntersection(LineLine apiLine2) {

	LineLine line2 = (LineLine) apiLine2;

	// Intersection of left and right
	if (a != line2.a) {
	    double tmpLeftX2 = ((leftB - line2.leftB) / (line2.a - a));
	    leftX2 = (int) tmpLeftX2;
	    leftZ2 = (int) ((line2.a * tmpLeftX2) + line2.leftB);
	    double tmpRightX2 = ((rightB - line2.rightB) / (line2.a - a));
	    rightX2 = (int) tmpRightX2;
	    rightZ2 = (int) ((line2.a * tmpRightX2) + line2.rightB);

	    // Change slope of line2
	    a2 = (line2.a1 + a2) / 2;
	    b2 = z2 - (a2 * x2);

	    // Modify the line 2 bottom
	    line2.a1 = a2;
	    line2.b1 = b2;
	    line2.leftX1 = leftX2;
	    line2.leftZ1 = leftZ2;
	    line2.rightX1 = rightX2;
	    line2.rightZ1 = rightZ2;
	}
    }

    /**
     *
     * @return
     */
    public long getVolume() {

	double leftLength = Math.sqrt((leftX2 - leftX1) ^ 2 + (leftZ2 - leftZ1) ^ 2);
	double rightLength = Math.sqrt((rightX2 - rightX1) ^ 2 + (rightZ2 - rightZ1) ^ 2);

	return (long) ((leftLength + rightLength) * (leftDist + rightDist) / 2 * (y2 - y1));
    }

    /**
     *
     * @param loc
     * @return
     */
    public boolean isLocationInside(Location loc) {

	return isLocationInside(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     *
     * @param locX
     * @param locY
     * @param locZ
     * @return
     */
    public boolean isLocationInside(int locX, int locY, int locZ) {

	int pos1;
	int pos2;

	if (!Calculate.isInInterval(locY, y1, y2)) {
	    return false;
	}

	// Calculate Z
	pos1 = (int) ((a * locX) + leftB);
	pos2 = (int) ((a * locX) + rightB);
	if (!Calculate.isInInterval(locZ, pos2, pos2)) {
	    return false;
	}
	pos1 = (int) ((a1 * locX) + b1);
	pos2 = (int) ((a2 * locX) + b2);
	if (!Calculate.isInInterval(locZ, pos1, pos2)) {
	    return false;
	}

	// Calculate X
	pos1 = (int) ((locZ - leftB) / a);
	pos2 = (int) ((locZ - rightB) / a);
	if (!Calculate.isInInterval(locX, pos1, pos2)) {
	    return false;
	}
	pos1 = (int) ((locZ - b1) / a1);
	pos2 = (int) ((locZ - b2) / a2);
	if (!Calculate.isInInterval(locX, pos1, pos2)) {
	    return false;
	}

	return true;
    }

    /**
     *
     * @return
     */
    public int getX1() {

	return x1;
    }

    /**
     *
     * @return
     */
    public int getY1() {

	return y1;
    }

    /**
     *
     * @return
     */
    public int getZ1() {

	return z1;
    }

    /**
     *
     * @return
     */
    public int getX2() {

	return x2;
    }

    /**
     *
     * @return
     */
    public int gety2() {

	return y2;
    }

    /**
     *
     * @return
     */
    public int getZ2() {

	return z2;
    }

    /**
     *
     * @return
     */
    public int getLeftX1() {

	return leftX1;
    }

    /**
     *
     * @return
     */
    public int getRightX1() {

	return rightX1;
    }

    /**
     *
     * @return
     */
    public int getLeftZ1() {

	return leftZ1;
    }

    /**
     *
     * @return
     */
    public int getRightZ1() {

	return rightZ1;
    }

    /**
     *
     * @return
     */
    public int getLeftX2() {

	return leftX2;
    }

    /**
     *
     * @return
     */
    public int getRightX2() {

	return rightX2;
    }

    /**
     *
     * @return
     */
    public int getLeftZ2() {

	return leftZ2;
    }

    /**
     *
     * @return
     */
    public int getRightZ2() {

	return rightZ2;
    }

    /**
     *
     * @return
     */
    public double getA() {

	return a;
    }

    /**
     *
     * @return
     */
    public double getB() {

	return b;
    }

    public LineLine copyOf() {
	return new LineLine(x1, y1, z1, x2, y2, z2, upDist, downDist, leftDist, rightDist);
    }
}
