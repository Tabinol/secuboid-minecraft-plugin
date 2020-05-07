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

public final class InventoryPotionEffectPojo {

    private final int inventoryEntryId;
    private final String name;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;

    public InventoryPotionEffectPojo(final int inventoryEntryId, final String name, final int duration,
            final int amplifier, final boolean ambient) {
        this.inventoryEntryId = inventoryEntryId;
        this.name = name;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
    }

    public int getInventoryEntryId() {
        return this.inventoryEntryId;
    }

    public String getName() {
        return this.name;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean getAmbient() {
        return this.ambient;
    }

    public boolean isAmbient() {
        return this.ambient;
    }
}