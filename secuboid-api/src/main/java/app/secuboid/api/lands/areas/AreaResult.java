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
package app.secuboid.api.lands.areas;

/**
 * Used for callback method when an area is created or modified. The area will
 * be returned only if it is a success.
 */
public interface AreaResult {

    /**
     * Gets the result code.
     *
     * @return the result code
     */
    AreaResultCode getCode();

    /**
     * Gets the area if success.
     *
     * @return the area or null
     */
    Area getArea();
}
