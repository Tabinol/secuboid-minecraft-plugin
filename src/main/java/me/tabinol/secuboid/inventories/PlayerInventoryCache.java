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
package me.tabinol.secuboid.inventories;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The player inventory status.
 */
public class PlayerInventoryCache {

    /**
     * By inventorie names survival inventories.
     */
    private final Map<String, PlayerInvEntry> inventoryNameToSurvivalInvEntry;

    /**
     * By inventorie names creative inventories.
     */
    private final Map<String, PlayerInvEntry> inventoryNameToCreativeInvEntry;

    /**
     * Death inventories.
     */
    private final List<PlayerInvEntry> deathInvEntry;

    /**
     * Current game mode and inventory.
     */
    private final Map.Entry<Boolean, InventorySpec> currentGameModeAndInventorySpec;

    public PlayerInventoryCache() {
        inventoryNameToSurvivalInvEntry = new HashMap<>();
        inventoryNameToCreativeInvEntry = new HashMap<>();
        deathInvEntry = new ArrayList<>();
        currentGameModeAndInventorySpec = new AbstractMap.SimpleEntry<>(null, null);
    }
}
