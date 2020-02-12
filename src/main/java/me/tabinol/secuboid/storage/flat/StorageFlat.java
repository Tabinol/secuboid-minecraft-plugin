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
package me.tabinol.secuboid.storage.flat;

import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.Storage;

/**
 * The Class StorageFlat.
 */
public class StorageFlat implements Storage {

    private final LandsFlat landsFlat;
    private final ApprovesFlat approvesFlat;
    private final PlayersCacheFlat playersCacheFlat;
    private final InventoriesFlat inventoriesFlat;

    public StorageFlat(final LandsFlat landsFlat, final ApprovesFlat approvesFlat,
            final PlayersCacheFlat playersCacheFlat, final InventoriesFlat inventoriesFlat) {
        this.landsFlat = landsFlat;
        this.approvesFlat = approvesFlat;
        this.playersCacheFlat = playersCacheFlat;
        this.inventoriesFlat = inventoriesFlat;
    }

    @Override
    public void loadAll() {
        loadLands();
        loadApproves();
        loadPlayersCache();
        loadInventories();
    }

    @Override
    public void loadLands() {
        landsFlat.loadLands();
    }

    @Override
    public void saveLand(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLand(final Land land) {
        landsFlat.removeLand(land);
    }

    @Override
    public void loadApproves() {
        approvesFlat.loadApproves();
    }

    @Override
    public void saveApprove(final Approve approve) {
        approvesFlat.saveApprove(approve);
    }

    @Override
    public void removeApprove(final Approve approve) {
        approvesFlat.removeApprove(approve);
    }

    @Override
    public void removeAllApproves() {
        approvesFlat.removeAll();
    }

    @Override
    public void loadInventories() {
        inventoriesFlat.loadInventories();
    }

    @Override
    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        inventoriesFlat.saveInventoryDefault(playerInvEntry);
    }

    @Override
    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        inventoriesFlat.removeInventoryDefault(playerInvEntry);
    }

    @Override
    public void loadInventoriesPlayer(final PlayerInventoryCache playerInventoryCache) {
        inventoriesFlat.loadInventoriesPlayer(playerInventoryCache);
    }

    @Override
    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        inventoriesFlat.saveInventoryPlayer(playerInvEntry);
    }

    @Override
    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        inventoriesFlat.saveInventoryPlayerDeath(playerInvEntry);
    }

    @Override
    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        inventoriesFlat.saveInventoryPlayerDeathHistory(playerInvEntry);
    }

    @Override
    public void loadPlayersCache() {
        playersCacheFlat.loadPlayersCache();
    }

    @Override
    public void savePlayerCacheEntry(final PlayerCacheEntry playerCacheEntry) {
        playersCacheFlat.savePlayersCache();
    }
}
