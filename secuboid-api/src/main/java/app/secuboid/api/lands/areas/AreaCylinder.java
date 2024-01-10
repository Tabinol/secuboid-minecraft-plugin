/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.secuboid.api.lands.areas;

/**
 * Represents a cylinder area.
 */
public interface AreaCylinder extends Area {

    /**
     * Gets the rx.
     *
     * @return the rx
     */
    double getRX();

    /**
     * Gets the rz.
     *
     * @return the rz
     */
    double getRZ();

    /**
     * Gets the origin H.
     *
     * @return the origin H
     */
    double getOriginH();

    /**
     * Gets the origin K.
     *
     * @return the origin K
     */
    double getOriginK();

    /**
     * Gets the z positive position from x.
     *
     * @param x the x
     * @return the position
     */
    int getZPosFromX(int x);

    /**
     * Gets the z negative position from x.
     *
     * @param x the x
     * @return the position
     */
    int getZNegFromX(int x);

    /**
     * Gets the x positive position from z.
     *
     * @param z the z
     * @return the position
     */
    int getXPosFromZ(int z);

    /**
     * Gets the x negative position from z.
     *
     * @param z the z
     * @return the position
     */
    int getXNegFromZ(int z);
}
