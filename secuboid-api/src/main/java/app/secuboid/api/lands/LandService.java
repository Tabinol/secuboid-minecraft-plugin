/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
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
package app.secuboid.api.lands;

import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.api.services.Service;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Services about lands.
 */
public interface LandService extends Service {

    /**
     * Creates the land with parent.
     *
     * @param parent   the parent or world
     * @param landName the land name
     * @param owner    the owner
     * @param area     the area
     * @param callback the method to call back if success or not
     */
    void create(Land parent, String landName, RecipientExec owner, Area area, Consumer<LandResult> callback);

    /**
     * Removes the land and force if the land has children. The children will be
     * orphan.
     *
     * @param land     the land
     * @param callback the method to call back if success or not
     */
    void removeForce(Land land, Consumer<LandResult> callback);

    /**
     * Removes the land and children recursively.
     *
     * @param land     the land
     * @param callback the method to call back if success or not
     */
    void removeRecursive(Land land, Consumer<LandResult> callback);

    /**
     * Removes the land.
     *
     * @param land     the land
     * @param callback the method to call back if success or not
     */
    void remove(Land land, Consumer<LandResult> callback);

    /**
     * Rename land.
     *
     * @param land     the land
     * @param newName  the new name
     * @param callback the method to call back if success or not
     */
    void rename(Land land, String newName, Consumer<LandResult> callback);

    /**
     * Sets the land parent.
     *
     * @param land      the land
     * @param newParent the land parent
     * @param callback  the method to call back if success or not
     */
    void setParent(Land land, Land newParent, Consumer<LandResult> callback);

    /**
     * Gets the land.
     *
     * @param id the id
     * @return the land
     */
    Land get(long id);

    /**
     * Gets the land.
     *
     * @return the land
     */
    Land get(Location loc);

    /**
     * Gets all lands available from a 2D location.
     *
     * @param world the world
     * @param x     the x
     * @param z     the z
     * @return the lands
     */
    Set<Land> getLands(World world, int x, int z);

    /**
     * Gets all lands available from a location.
     *
     * @param loc the location
     * @return the lands
     */
    Set<Land> getLands(Location loc);

    /**
     * Gets the active location path (area or world land) from the location.
     *
     * @return the last location path (area or world land instance)
     */
    LocationPath getLocationPath(Location loc);

    /**
     * Gets the world land from the location.
     *
     * @param loc the loc
     * @return the world land
     */
    Land getWorldLand(Location loc);

    /**
     * Gets the world land from the world.
     *
     * @param world the world
     * @return the world land
     */
    Land getWorldLand(World world);
}
