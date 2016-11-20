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
package me.tabinol.secuboid.selection.region;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.selection.PlayerSelection;
import me.tabinol.secuboid.selection.visual.VisualSelection;
import org.bukkit.entity.Player;

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
         * Move with the player
         */
        ACTIVE,
        /**
         * Expend selection
         */
        EXPAND

    }

    private final MoveType moveType;

    /**
     * The area.
     */
    private final VisualSelection visualSelection;

    /**
     * Instantiates a new area selection.
     *
     * @param secuboid   secuboid instance
     * @param player     the player
     * @param area       the area (null will create the area from player)
     * @param isFromLand is from land or must be false
     * @param areaType   area type (can be null if the area is not null)
     * @param moveType   move type
     */
    public AreaSelection(Secuboid secuboid, Player player, Area area, boolean isFromLand,
                         AreaType areaType, MoveType moveType) {

        this.moveType = moveType;

        if (area == null) {
            visualSelection = secuboid.getNewInstance().createVisualSelection(areaType,
                    isFromLand, player);
            visualSelection.setActiveSelection();
        } else {
            visualSelection = secuboid.getNewInstance().createVisualSelection(area,
                    isFromLand, player);
            visualSelection.makeVisualSelection();
        }
    }

    @Override
    public PlayerSelection.SelectionType getSelectionType() {
        return PlayerSelection.SelectionType.AREA;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.selection.region.RegionSelection#removeSelection()
     */
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
}
