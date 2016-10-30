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

import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.LinesArea;
import org.bukkit.entity.Player;

/**
 * Utility class for visual selections (static)
 *
 * @author tabinol
 */
public class VisualSelectionUtil {

    /**
     * Create a new visual selection from default
     *
     * @param areaType areaType
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(AreaType areaType, boolean isFromLand, Player player) {

	switch (areaType) {
	    case CUBOID:
		return new VisualSelectionCuboid(null, isFromLand, player);
	    case CYLINDER:
		return new VisualSelectionCylinder(null, isFromLand, player);
	    case LINES:
		return new VisualSelectionCylinder(null, isFromLand, player);
	    default:
		return null;
	}
    }

    /**
     * Create a visual selection from an area
     *
     * @param area area
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(Area area, boolean isFromLand, Player player) {

	switch (area.getAreaType()) {
	    case CUBOID:
		return new VisualSelectionCuboid((CuboidArea) area, isFromLand, player);
	    case CYLINDER:
		return new VisualSelectionCylinder((CylinderArea) area, isFromLand, player);
	    case LINES:
		return new VisualSelectionLines((LinesArea) area, isFromLand, player);
	    default:
		return null;
	}
    }
}
