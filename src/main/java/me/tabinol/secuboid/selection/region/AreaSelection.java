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
package me.tabinol.secuboid.selection.region;

import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.selection.PlayerSelection;
import me.tabinol.secuboid.selection.visual.VisualSelection;
import me.tabinol.secuboid.selection.visual.VisualSelectionCuboid;
import me.tabinol.secuboid.selection.visual.VisualSelectionCylinder;
import me.tabinol.secuboid.selection.visual.VisualSelectionRoad;

/**
 * The Class AreaSelection.
 */
public class AreaSelection implements RegionSelection {

    /**
     * Move Type.
     */
    public enum MoveType {

        /**
         * Fixed
         */
        PASSIVE,
        /**
         * Expand (default)
         */
        EXPAND,
        /**
         * Retract
         */
        RETRACT,
        /**
         * Mode with player
         */
        MOVE
    }

    private final Secuboid secuboid;

    private final MoveType moveType;

    /**
     * The area.
     */
    private final VisualSelection visualSelection;

    /**
     * Instantiates a new area selection.
     *
     * @param secuboid     secuboid instance
     * @param player       the player
     * @param area         the area (null will create the area from player)
     * @param originalArea the original area from a land for expand
     * @param isActive     is from land (active) or must be false (passive)
     * @param areaType     area type (can be null if the area is not null)
     * @param moveType     move type
     */
    public AreaSelection(final Secuboid secuboid, final Player player, final Area area, final Area originalArea,
            final boolean isActive, final AreaType areaType, final MoveType moveType) {
        this.secuboid = secuboid;
        this.moveType = moveType;

        if (area == null) {
            visualSelection = createVisualSelection(areaType, isActive, player);
            visualSelection.setActiveSelection();
        } else {
            visualSelection = createVisualSelection(area, originalArea, isActive, player);
            visualSelection.makeVisualSelection();
        }
    }

    @Override
    public PlayerSelection.SelectionType getSelectionType() {
        return PlayerSelection.SelectionType.AREA;
    }

    @Override
    public void removeSelection() {
        visualSelection.removeSelection();
    }

    /**
     * Gets the visual selection.
     *
     * @return the visual selection
     */
    public VisualSelection getVisualSelection() {
        return visualSelection;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    /**
     * Called from then player listener
     */
    public void playerMove() {
        visualSelection.playerMove(moveType);
    }

    /**
     * Create a new visual selection from default
     *
     * @param areaType   areaType
     * @param isFromLand is from land or must be false
     * @param player     the player
     * @return visual selection
     */
    private VisualSelection createVisualSelection(final AreaType areaType, final boolean isFromLand,
            final Player player) {

        switch (areaType) {
            case CUBOID:
                return new VisualSelectionCuboid(secuboid, null, null, isFromLand, player);
            case CYLINDER:
                return new VisualSelectionCylinder(secuboid, null, null, isFromLand, player);
            case ROAD:
                return new VisualSelectionRoad(secuboid, null, null, isFromLand, player);
            default:
                return null;
        }
    }

    /**
     * Create a visual selection from an area
     *
     * @param area         area
     * @param originalArea the original area from a land for expand (in this case,
     *                     area must be a copy of)
     * @param isFromLand   is from land or must be false
     * @param player       the player
     * @return visual selection
     */
    private VisualSelection createVisualSelection(final Area area, final Area originalArea, final boolean isFromLand,
            final Player player) {

        switch (area.getAreaType()) {
            case CUBOID:
                return new VisualSelectionCuboid(secuboid, (CuboidArea) area, (CuboidArea) originalArea, isFromLand,
                        player);
            case CYLINDER:
                return new VisualSelectionCylinder(secuboid, (CylinderArea) area, (CylinderArea) originalArea,
                        isFromLand, player);
            case ROAD:
                return new VisualSelectionRoad(secuboid, (RoadArea) area, (RoadArea) originalArea, isFromLand, player);
            default:
                return null;
        }
    }
}
