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
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection.MoveType;
import org.bukkit.entity.Player;


/**
 * The Class LandSelection.
 */
public class LandSelection extends RegionSelection {

    /** The land. */
    private final Land land;
    
    /** The visual areas. */
    private final TreeMap<Area, AreaSelection> visualAreas; // Visuals arealist
    
    /**
     * Instantiates a new land selection.
     *
     * @param player the player
     * @param land the land
     */
    public LandSelection(Player player, Land land) {
        
        super(SelectionType.LAND, player);
        this.land = land;
        visualAreas = new TreeMap<Area, AreaSelection>();
        
        // Add visual areas
        for(Area area : land.getAreas()) {
            visualAreas.put(area, new AreaSelection(player, area, true, 
                    null, MoveType.PASSIVE));
        }
    }
    
    /**
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {
        
        return land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.selection.region.RegionSelection#removeSelection()
     */
    @Override
    public void removeSelection() {

        for(AreaSelection areaSel : visualAreas.values()) {
            areaSel.removeSelection();
        }
        
        visualAreas.clear();
    }
}
