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
package me.tabinol.secuboid.selection;

import java.util.Collection;
import java.util.EnumMap;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.LandSelection;
import me.tabinol.secuboid.selection.region.RegionSelection;

/**
 * The Class PlayerSelection.
 */
public class PlayerSelection {

    /**
     * Selection Type.
     */
    public enum SelectionType { // ACTIVE = move with the player, PASSIVE = fixed

        /**
         * The land.
         */
        LAND,
        /**
         * The area.
         */
        AREA
    }

    private final Secuboid secuboid;

    /**
     * The player conf entry.
     */
    private final PlayerConfEntry playerConfEntry;

    /**
     * The selection list.
     */
    private final EnumMap<SelectionType, RegionSelection> selectionList; // SelectionList for the player

    /**
     * Instantiates a new player selection.
     *
     * @param secuboid        secuboid instance
     * @param playerConfEntry the player conf entry
     */
    public PlayerSelection(final Secuboid secuboid, final PlayerConfEntry playerConfEntry) {

        this.secuboid = secuboid;
        this.playerConfEntry = playerConfEntry;
        selectionList = new EnumMap<SelectionType, RegionSelection>(SelectionType.class);
    }

    /**
     * Checks for selection.
     *
     * @return true, if successful
     */
    public boolean hasSelection() {

        return !selectionList.isEmpty();
    }

    /**
     * Gets the selections.
     *
     * @return the selections
     */
    public Collection<RegionSelection> getSelections() {

        return selectionList.values();
    }

    /**
     * Adds the selection.
     *
     * @param sel the sel
     */
    public void addSelection(final RegionSelection sel) {

        selectionList.put(sel.getSelectionType(), sel);
    }

    /**
     * Gets the selection.
     *
     * @param type the type
     * @return the selection
     */
    public RegionSelection getSelection(final SelectionType type) {

        return selectionList.get(type);
    }

    /**
     * Removes the selection.
     *
     * @param type the type
     * @return the region selection
     */
    public RegionSelection removeSelection(final SelectionType type) {

        final RegionSelection select = selectionList.remove(type);

        if (select != null) {
            select.removeSelection();
        }

        return select;
    }

    /**
     * Refresh land selection.
     */
    public void refreshLand() {

        final Land land = getLand();

        if (land != null) {
            removeSelection(SelectionType.LAND);
            addSelection(new LandSelection(secuboid, playerConfEntry.getPlayer(), land));
        }
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {

        final LandSelection sel = (LandSelection) selectionList.get(SelectionType.LAND);
        if (sel != null) {
            return sel.getLand();
        } else {
            return null;
        }
    }

    /**
     * Gets the area.
     *
     * @return the cuboid area
     */
    public Area getArea() {

        final AreaSelection sel = (AreaSelection) selectionList.get(SelectionType.AREA);
        if (sel != null) {
            return sel.getVisualSelection().getArea();
        } else {
            return null;
        }
    }

    /**
     * Gets the area to replace.
     *
     * @return the area to replace
     */
    public Area getAreaToReplace() {

        final AreaSelection sel = (AreaSelection) selectionList.get(SelectionType.AREA);
        if (sel != null) {
            return sel.getVisualSelection().getOriginalArea();
        } else {
            return null;
        }
    }
}
