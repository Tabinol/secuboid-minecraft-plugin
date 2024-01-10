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

/**
 * Represents a location path: /world/land1/land2:areaId
 */
public interface LocationPath {

    /**
     * Used for land separator.
     */
    public static final String SEPARATOR_LAND = "/";

    /**
     * Used for separator before the area id.
     */
    public static final String SEPARATOR_AREA = ":";

    /**
     * Configuration set prefix.
     */
    public static final String PREFIX_CONFIGURATION_SET = "@";

    /**
     * Gets the name<br>
     * world: world<br>
     * land: land<br>
     * Area: 1<br>
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the complete path name<br>
     * /world for a world<br>
     * /world/land for a land<br>
     * /world/land:1 for an area<br>
     *
     * @return the complete path name
     */
    String getPathName();
}
