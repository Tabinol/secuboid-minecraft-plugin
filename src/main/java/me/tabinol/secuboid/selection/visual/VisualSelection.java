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
package me.tabinol.secuboid.selection.visual;

import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.selection.region.AreaSelection;

/**
 *
 * @author michel
 */
public interface VisualSelection {

    /**
     *
     * @return
     */
    Area getArea();

    /**
     * Sets the active selection.
     */
    void setActiveSelection();

    /**
     * Make visual selection.
     */
    void makeVisualSelection();

    // Called from AreaSelection then player listenner
    /**
     *
     * @param moveType
     */
    void playerMove(AreaSelection.MoveType moveType);

    /**
     * Gets the if the area has collision.
     *
     * @return the collision
     */
    boolean hasCollision();

    /**
     *
     */
    public void removeSelection();

    /**
     *
     * @return
     */
    RealLand getParentDetected();
}
