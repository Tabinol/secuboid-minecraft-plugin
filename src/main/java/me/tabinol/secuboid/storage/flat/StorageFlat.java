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
package me.tabinol.secuboid.storage.flat;

import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
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
    public boolean loadAll() {
        loadPlayersCache();
        loadLands();
        loadApproves();
        loadInventories();

        return false;
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
    public void removeLandArea(final Land land, final Area area) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandArea(final Land land, final Area area) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLandBanned(final Land land, final PlayerContainer playerContainer) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandBanned(final Land land, final PlayerContainer playerContainer) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLandFlag(final Land land, final Flag flag) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeAllLandFlags(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandFlag(final Land land, final Flag flag) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeAllLandPermissions(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeAllLandPlayerNotify(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLandResident(final Land land, final PlayerContainer playerContainer) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeAllLandResidents(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void saveLandResident(final Land land, final PlayerContainer playerContainer) {
        landsFlat.saveLand(land);
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
