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
package me.tabinol.secuboid.exceptions;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;

/**
 * The Class SecuboidLandException.
 */
public class SecuboidLandException extends ApiSecuboidLandException {

    private static final long serialVersionUID = -4561559858019587492L;

    /**
     * Instantiates a new secuboid land exception.
     *
     * @param secuboid the secuboid instance
     * @param landName the land name
     * @param area     the area
     * @param action   the action
     * @param error    the error
     */
    public SecuboidLandException(final Secuboid secuboid, final String landName, final Area area,
            final Collisions.LandAction action, final Collisions.LandError error) {
        super(String.format("Secuboid Land Exception: [Land=%s, area=%s, action=%s, error=%s]", landName,
                area.getPrint(), action.name(), error.name()));
    }

    /**
     * Instantiates a new secuboid land exception.
     *
     * @param err the error message
     */
    public SecuboidLandException(final String err) {
        super(err);
    }
}
