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
package me.tabinol.secuboid.storage;

import java.util.UUID;

import me.tabinol.secuboid.lands.RealLand;

/**
 * The Interface Storage.
 */
public interface Storage {

    /**
     * Load all.
     */
    void loadAll();

    /**
     * Save land.
     *
     * @param land the land
     */
    void saveLand(RealLand land);

    /**
     * Removes the land.
     *
     * @param land the land
     */
    void removeLand(RealLand land);

    /**
     * Removes the land.
     *
     * @param landUUID      the land name uuid
     * @param landGenealogy The land genealogy
     */
    void removeLand(UUID landUUID, int landGenealogy);

    /**
     * Load lands.
     */
    void loadLands();
}
