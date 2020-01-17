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
package me.tabinol.secuboid.playerscache;

import java.util.UUID;

import me.tabinol.secuboid.playerscache.PlayersCache.PlayerCacheable;
import me.tabinol.secuboid.storage.Savable;

/**
 * The Class PlayerCacheEntry.
 */
public class PlayerCacheEntry implements Savable, PlayerCacheable {

    /**
     * The uuid.
     */
    private final UUID uuid;

    /**
     * The case sensitive name.
     */
    private final String caseSensitiveName;

    /**
     * Instantiates a new player cache entry.
     *
     * @param uuid              the uuid
     * @param caseSensitiveName the case sensitive name
     */
    public PlayerCacheEntry(final UUID uuid, final String caseSensitiveName) {

        this.uuid = uuid;
        this.caseSensitiveName = caseSensitiveName;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    @Override
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return caseSensitiveName;
    }
}
