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
package me.tabinol.secuboid.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.tabinol.secuboid.storage.Savable;

/**
 * The player inventory status.
 */
public class PlayerInventoryCache implements Savable {

    public static final int DEATH_SAVE_MAX_NBR = 9;

    private final UUID playerUUID;
    private final String playerName;

    /**
     * By inventory specs survival inventories.
     */
    private final Map<InventorySpec, PlayerInvEntry> inventorySpecToSurvivalInvEntry;

    /**
     * By inventory specs creative inventories.
     */
    private final Map<InventorySpec, PlayerInvEntry> inventorySpecToCreativeInvEntry;

    /**
     * Death inventories.
     */
    private final List<PlayerInvEntry> deathInvEntries;

    /**
     * Current game mode and inventory.
     */
    private PlayerInvEntry curInvEntry;

    public PlayerInventoryCache(final UUID playerUUID, final String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        inventorySpecToSurvivalInvEntry = new HashMap<>();
        inventorySpecToCreativeInvEntry = new HashMap<>();
        deathInvEntries = new ArrayList<>();
        curInvEntry = null;
    }

    public void addInventorySurvival(final InventorySpec inventorySpec, final PlayerInvEntry playerInvEntry) {
        inventorySpecToSurvivalInvEntry.put(inventorySpec, playerInvEntry);
    }

    PlayerInvEntry getInventorySurvival(final InventorySpec inventorySpec) {
        return inventorySpecToSurvivalInvEntry.get(inventorySpec);
    }

    public void addInventoryCreative(final InventorySpec inventorySpec, final PlayerInvEntry playerInvEntry) {
        inventorySpecToCreativeInvEntry.put(inventorySpec, playerInvEntry);
    }

    PlayerInvEntry getInventoryCreative(final InventorySpec inventorySpec) {
        return inventorySpecToCreativeInvEntry.get(inventorySpec);
    }

    public void addInventoryDeath(final int deathVersion, final PlayerInvEntry playerInvEntry) {
        deathInvEntries.add(deathVersion - 1, playerInvEntry);
    }

    PlayerInvEntry getInventoryDeath(final int deathVersion) {
        return deathInvEntries.get(deathVersion);
    }

    public PlayerInvEntry getCurInvEntry() {
        return curInvEntry;
    }

    PlayerInventoryCache setCurInvEntry(final PlayerInvEntry curInvEntry) {
        this.curInvEntry = curInvEntry;
        return this;
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public UUID getUUID() {
        return playerUUID;
    }
}
