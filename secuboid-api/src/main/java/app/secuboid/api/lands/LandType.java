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

package app.secuboid.api.lands;

import java.util.stream.Stream;

/**
 * Represents a land type.
 */
@SuppressWarnings("LombokGetterMayBeUsed")
public enum LandType {

    /**
     * Real land
     */
    LAND("l"),

    /**
     * Configuration set for one or more lands
     */
    CONFIGURATION_SET("s"),

    /**
     * Land on the entire world
     */
    WORLD("w");

    private final String value;

    private LandType(String value) {
        this.value = value;
    }

    /**
     * Gets the type from value.
     *
     * @param value value
     * @return the type
     */
    public static LandType of(String value) {
        return Stream.of(LandType.values())
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
