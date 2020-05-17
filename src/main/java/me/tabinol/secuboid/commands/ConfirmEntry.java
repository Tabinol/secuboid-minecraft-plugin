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
package me.tabinol.secuboid.commands;

import me.tabinol.secuboid.lands.Land;

/**
 * The Class ConfirmEntry.
 */
public class ConfirmEntry {

    /**
     * The Enum ConfirmType. Represent a Entry for a "/secuboid confirm".
     */
    public enum ConfirmType {

        /**
         * The remove land.
         */
        REMOVE_LAND,
        /**
         * The remove area.
         */
        REMOVE_AREA,
        /**
         * The land default.
         */
        LAND_DEFAULT;
    }

    /**
     * The confirm type.
     */
    public final ConfirmType confirmType;

    /**
     * The land.
     */
    public final Land land;

    /**
     * The area nb.
     */
    public final int areaNb;

    /**
     * Instantiates a new confirm entry.
     *
     * @param confirmType the confirm type
     * @param land        the land
     * @param areaNb      the area nb
     */
    public ConfirmEntry(ConfirmType confirmType, Land land, int areaNb) {

        this.confirmType = confirmType;
        this.land = land;
        this.areaNb = areaNb;
    }
}
