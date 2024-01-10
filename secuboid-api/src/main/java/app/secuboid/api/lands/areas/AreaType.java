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

import java.util.stream.Stream;

/**
 * Represents an area type.
 */
@SuppressWarnings("LombokGetterMayBeUsed")
public enum AreaType {

    /**
     * Cuboid area
     */
    CUBOID("c"),

    /**
     * Cylinder area
     */
    CYLINDER("y");

    private final String value;

    private AreaType(String value) {
        this.value = value;
    }

    /**
     * Gets the type from value.
     *
     * @param value value
     * @return the type
     */
    public static AreaType of(String value) {
        return Stream.of(AreaType.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
