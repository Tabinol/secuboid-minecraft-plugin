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
package me.tabinol.secuboid.selection.visual;

import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.selection.region.AreaSelection;

/**
 * Represents a visual selection.
 */
public interface VisualSelection {

    /**
     * Gets the area.
     * 
     * @return the area
     */
    Area getArea();

    /**
     * Gets the origin area to modify if exist.
     * 
     * @return the origine area
     */
    Area getOriginalArea();

    /**
     * Sets the active selection.
     */
    void setActiveSelection();

    /**
     * Make visual selection.
     */
    void makeVisualSelection();

    /**
     * Called from AreaSelection then player listener.
     *
     * @param moveType the type of move
     */
    void playerMove(AreaSelection.MoveType moveType);

    /**
     * Gets the if the area has collision.
     *
     * @return the collision
     */
    boolean hasCollision();

    /**
     * Removes the selection.
     */
    void removeSelection();

    /**
     * Gets if the parent is detected. The parent can be the world if there is no
     * parent.
     *
     * @return the detected land permissions flags
     */
    LandPermissionsFlags getParentPermsFlagsDetected();
}
