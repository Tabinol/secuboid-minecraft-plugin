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

import app.secuboid.api.services.Service;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

/**
 * Services about areas.
 */
public interface AreaService extends Service {

    /**
     * Gets the areas. This method ignore Y value.
     *
     * @param world the world
     * @param x     the x
     * @param z     the z
     * @return the areas
     */
    Set<Area> getAreas(World world, int x, int z);

    /**
     * Gets the areas.
     *
     * @param world the world
     * @param x     the x
     * @param y     the y
     * @param z     the z
     * @return the areas
     */
    Set<Area> getAreas(World world, int x, int y, int z);

    /**
     * Gets all areas from a location.
     *
     * @param loc the loc
     * @return the areas
     */
    Set<Area> getAreas(Location loc);

    /**
     * Gets the active area from the location.
     *
     * @param loc the loc
     * @return the last area or null if the location is outside an area
     */
    Area getArea(Location loc);

}
