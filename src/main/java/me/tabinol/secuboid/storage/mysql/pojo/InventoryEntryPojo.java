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

import org.bukkit.inventory.ItemStack;

public final class InventoryEntryPojo {

    private final int id;
    private final int level;
    private final float exp;
    private final double health;
    private final int foodLevel;
    private final ItemStack[] contents;
    private final ItemStack[] enderChestContents;

    public InventoryEntryPojo(final int id, final int level, final float exp, final double health, final int foodLevel,
            final ItemStack[] contents, final ItemStack[] enderChestContents) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.health = health;
        this.foodLevel = foodLevel;
        this.contents = contents;
        this.enderChestContents = enderChestContents;
    }

    public int getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public float getExp() {
        return this.exp;
    }

    public double getHealth() {
        return this.health;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public ItemStack[] getEnderChestContents() {
        return this.enderChestContents;
    }
}
