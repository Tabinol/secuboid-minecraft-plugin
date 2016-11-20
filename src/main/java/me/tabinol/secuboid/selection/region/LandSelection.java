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

import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.selection.PlayerSelection;
import me.tabinol.secuboid.selection.region.AreaSelection.MoveType;
import org.bukkit.entity.Player;

/**
 * The Class LandSelection.
 */
public class LandSelection implements RegionSelection {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The land.
     */
    private final RealLand land;

    /**
     * The visual areas.
     */
    private final TreeMap<Area, AreaSelection> visualAreas; // Visuals arealist

    /**
     * Instantiates a new land selection.
     *
     * @param secuboid secuboid instance
     * @param player   the player
     * @param land     the land
     */
    public LandSelection(Secuboid secuboid, Player player, RealLand land) {

        this.player = player;
        this.land = land;
        visualAreas = new TreeMap<Area, AreaSelection>();

        // Add visual areas
        for (Area area : land.getAreas()) {
            visualAreas.put(area, new AreaSelection(secuboid, player, area, true, null, MoveType.PASSIVE));
        }
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public RealLand getLand() {
        return land;
    }

    @Override
    public PlayerSelection.SelectionType getSelectionType() {
        return PlayerSelection.SelectionType.LAND;
    }

    @Override
    public void removeSelection() {

        for (AreaSelection areaSel : visualAreas.values()) {
            areaSel.removeSelection();
        }

        visualAreas.clear();
    }
}
