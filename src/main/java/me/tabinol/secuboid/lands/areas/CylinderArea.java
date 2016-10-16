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

import me.tabinol.secuboid.utilities.Calculate;

/**
 * Represents a cylinder area.
 */
public final class CylinderArea extends Area {

    private double rX;
    private double rZ;
    private double originH;
    private double originK;

    /**
     * Instantiates a new cylinder area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     */
    public CylinderArea(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        super(AreaType.CYLINDER, worldName, x1, y1, z1, x2, y2, z2);
        updatePos();
    }
    
    /**
     * Sets the x1
     * @param x1 x1
     */
    public void setX1(int x1) {

        this.x1 = x1;
        updatePos();
    }

    /**
     * Sets the x2
     * @param x2 x2
     */
    public void setX2(int x2) {

        this.x2 = x2;
        updatePos();
    }

    /**
     * Sets the y1
     * @param y1 y1
     */
    public void setY1(int y1) {

        this.y1 = y1;
        updatePos();
    }

    /**
     * Sets the y2
     * @param y2 y2
     */
    public void setY2(int y2) {

        this.y2 = y2;
        updatePos();
    }

    /**
     * Sets the z1
     * @param z1 z1
     */
    public void setZ1(int z1) {

        this.z1 = z1;
        updatePos();
    }

    /**
     * Sets the z2
     * @param z2 z2
     */
    public void setZ2(int z2) {

        this.z2 = z2;
        updatePos();
    }

    public double getRX() {
        
        return rX;
    }
    
    public double getRZ() {
        
        return rZ;
    }
    
    public double getOriginH() {
        
        return originH;
    }
    
    public double getOriginK() {
        
        return originK;
    }

    private void updatePos() {

        // Use "this", x2 must be greater of x1, etc.
        rX = (double) (x2 - x1 + 1) / 2;
        rZ = (double) (z2 - z1 + 1) / 2;
        originH = x1 + (rX);
        originK = z1 + (rZ);
    }

    /**
     * Copy of.
     *
     * @return the cylinder area
     */
    public CylinderArea copyOf() {

        return new CylinderArea(worldName, x1, y1, z1, x2, y2, z2);
    }

    public int getZPosFromX(int x) {
        
        return (int) ((rX * originK + rZ *
                Math.sqrt(Math.pow(-x, 2) + 2 * originH * x - Math.pow(originH, 2) + Math.pow(rX, 2))) / rX);
    }
    
    public int getZNegFromX(int x) {
        
        return (int) -((-rX * originK + rZ * 
                Math.sqrt(Math.pow(-x, 2) + 2 * originH * x - Math.pow(originH, 2) + Math.pow(rX, 2))) / rX);
    }
    
    /**
     * Gets the volume.
     *
     * @return the volume (in block)
     */
    public long getVolume() {

        return (long) (rX * (y2 - y1 + 1) * rZ * Math.PI);
    }

    public boolean isLocationInside(String worldName, int x, int y, int z) {

        return worldName.equals(worldName)
                && Calculate.isInInterval(y, y1, y2)
                && ((Math.pow((x - originH), 2) / Math.pow(rX, 2)) + (Math.pow((z - originK), 2) / Math.pow(rZ, 2))) < 1;
    }
}
