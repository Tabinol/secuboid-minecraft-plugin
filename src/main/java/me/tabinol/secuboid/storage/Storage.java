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

import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.flat.ApprovesFlat;
import me.tabinol.secuboid.storage.flat.InventoriesFlat;
import me.tabinol.secuboid.storage.flat.LandsFlat;
import me.tabinol.secuboid.storage.flat.PlayersCacheFlat;
import me.tabinol.secuboid.storage.flat.StorageFlat;

/**
 * The Interface Storage.
 */
public interface Storage {

    static Storage getStorageFromConfig(final Secuboid secuboid, final String configParam) {
        final Storage storage;

        switch (configParam.toLowerCase()) {
        default: // No break because dafault execute "flat"
            secuboid.getLogger().log(Level.WARNING, () -> String
                    .format("The storage type \"%s\" is not available, using default \"flat\"", configParam));
        case "flat":
            final LandsFlat landsFlat = new LandsFlat(secuboid);
            final ApprovesFlat approvesFlat = new ApprovesFlat(secuboid);
            final PlayersCacheFlat playersCacheFlat = new PlayersCacheFlat(secuboid);
            final InventoriesFlat inventoriesFlat = new InventoriesFlat(secuboid);
            storage = new StorageFlat(landsFlat, approvesFlat, playersCacheFlat, inventoriesFlat);
        }
        return storage;
    }

    /**
     * Load all.
     */
    void loadAll();

    /**
     * Load lands.
     */
    void loadLands();

    /**
     * Save land.
     *
     * @param land the land
     */
    void saveLand(Land land);

    /**
     * Removes the land.
     *
     * @param land the land
     */
    void removeLand(Land land);

    /**
     * Load approves.
     */
    void loadApproves();

    /**
     * Save approve.
     *
     * @param approve the approve
     */
    void saveApprove(Approve approve);

    /**
     * Removes the approve.
     *
     * @param landUUID the approve
     */
    void removeApprove(Approve approve);

    /**
     * Removes all approves.
     */
    void removeAllApproves();

    /**
     * Load all inventories (except player specific).
     */
    void loadInventories();

    /**
     * Save default inventory.
     * 
     * @param playerInvEntry the inventory
     */
    void saveInventoryDefault(PlayerInvEntry playerInvEntry);

    /**
     * Load all inventories for a specific player.
     * 
     * @param playerConfEntry the player configuration entry
     */
    void loadInventoriesPlayer(PlayerConfEntry playerConfEntry);

    /**
     * Save a specific inventory for a player.
     * 
     * @param playerInvEntry the player inventory.
     */
    void saveInventoryPlayer(PlayerInvEntry playerInvEntry);

    /**
     * Save a specific inventory for a death player. (Just the Ender chest)
     * 
     * @param playerInvEntry the player inventory.
     */
    void saveInventoryPlayerDeath(PlayerInvEntry playerInvEntry);

    /**
     * Save a specific inventory for a death player (for restore).
     * 
     * @param playerInvEntry the player inventory.
     */
    void saveInventoryPlayerDeathHistory(PlayerInvEntry playerInvEntry);

    /**
     * Load players cache.
     */
    void loadPlayersCache();

    /**
     * Save the player cache entry.
     * 
     * @param playerCacheEntry the player cache entry
     */
    void savePlayerCacheEntry(PlayerCacheEntry playerCacheEntry);
}
