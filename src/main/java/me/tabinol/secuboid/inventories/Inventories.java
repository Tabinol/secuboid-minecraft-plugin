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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Inventories store
 */
public final class Inventories {

    private static class PlayerInvInfo {
        final Map<String, PlayerInvEntry> InventoryNameToSurvivalInvEntry;
        final Map<String, PlayerInvEntry> InventoryNameToCreativeInvEntry;
        final List<PlayerInvEntry> DeathInvEntry;

        PlayerInvInfo() {
            InventoryNameToSurvivalInvEntry = new HashMap<>();
            InventoryNameToCreativeInvEntry = new HashMap<>();
            DeathInvEntry = new ArrayList<>();
        }
    }

    private final Map<String, PlayerInvEntry> InventoryNameToDefaultInvEntry;
    private final Map<UUID, PlayerInvInfo> playerUUIDToPlayerInvInfo;

    public Inventories() {
        InventoryNameToDefaultInvEntry = new HashMap<>();
        playerUUIDToPlayerInvInfo = new HashMap<>();
    }
}