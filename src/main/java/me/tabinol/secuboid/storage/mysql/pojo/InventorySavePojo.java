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
package me.tabinol.secuboid.storage.mysql.pojo;

import java.util.UUID;

public final class InventorySavePojo {

    private final UUID playerUUID;
    private final int inventoryId;
    private final int gameModeId;
    private final int inventoryEntryId;

    public InventorySavePojo(final UUID playerUUID, final int inventoryId, final int gameModeId,
            final int inventoryEntryId) {
        this.playerUUID = playerUUID;
        this.inventoryId = inventoryId;
        this.gameModeId = gameModeId;
        this.inventoryEntryId = inventoryEntryId;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public int getInventoryId() {
        return this.inventoryId;
    }

    public int getGameModeId() {
        return this.gameModeId;
    }

    public int getInventoryEntryId() {
        return this.inventoryEntryId;
    }
}