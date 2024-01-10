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

package app.secuboid.api.persistence;

/**
 * Represents any Secuboid object that contains an id and a JPA. It is used to remember the row id and JPA object.
 */
public interface WithId {

    /**
     * Represents a non-existing id or not yet in the database.
     */
    public static final long NON_EXISTING_ID = -1L;

    /**
     * Gets the object id.
     *
     * @return the id
     */
    default long getId() {
        return getJPA().getId();
    }

    /**
     * Gets tne JPA object.
     *
     * @return the JPA object
     */
    JPA getJPA();
}
