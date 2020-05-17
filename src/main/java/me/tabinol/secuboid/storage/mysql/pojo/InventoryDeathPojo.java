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

public final class InventoryDeathPojo {

    private final long playerUUID;
    private final long inventoryId;
    private final long gameModeId;
    private final int deathNumber;
    private final long inventoryEntryId;

    public InventoryDeathPojo(final long playerUUID, final long inventoryId, final long gameModeId,
            final int deathNumber, final long inventoryEntryId) {
        this.playerUUID = playerUUID;
        this.inventoryId = inventoryId;
        this.gameModeId = gameModeId;
        this.deathNumber = deathNumber;
        this.inventoryEntryId = inventoryEntryId;
    }

    public long getPlayerUUID() {
        return this.playerUUID;
    }

    public long getInventoryId() {
        return this.inventoryId;
    }

    public long getGameModeId() {
        return this.gameModeId;
    }

    public int getDeathNumber() {
        return this.deathNumber;
    }

    public long getInventoryEntryId() {
        return this.inventoryEntryId;
    }
}