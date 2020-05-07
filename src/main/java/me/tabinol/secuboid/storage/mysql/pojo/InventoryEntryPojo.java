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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.PlayerInvEntry;

public final class InventoryEntryPojo {

    private static final String PATH_PREFIX_SLOT = "slot.";
    private static final String PATH_PREFIX_ARMOR = "armor.";
    private static final String PATH_PREFIX_ENDER_CHEST = "enderchest.";
    private static final String PATH_OFF_HAND = "offhand.0";

    private final int id;
    private final int level;
    private final float exp;
    private final double health;
    private final int foodLevel;
    private final String itemStackStr;

    public InventoryEntryPojo(final int id, final int level, final float exp, final double health, final int foodLevel,
            final String itemStackStr) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.health = health;
        this.foodLevel = foodLevel;
        this.itemStackStr = itemStackStr;
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

    public String getItemStackStr() {
        return this.itemStackStr;
    }

    public static String itemStackToText(final ItemStack[] slotItems, final ItemStack[] armorItems,
            final ItemStack[] enderChestItems, final ItemStack itemOffhand, final boolean enderChestOnly) {
        final YamlConfiguration itemStackYaml = new YamlConfiguration();

        if (enderChestOnly) {
            for (int t = 0; t < enderChestItems.length; t++) {
                itemStackYaml.set(PATH_PREFIX_ENDER_CHEST + t, enderChestItems[t]);
            }
        } else {
            for (int t = 0; t < slotItems.length; t++) {
                itemStackYaml.set(PATH_PREFIX_SLOT + t, slotItems[t]);
            }
            for (int t = 0; t < armorItems.length; t++) {
                itemStackYaml.set(PATH_PREFIX_ARMOR + t, armorItems[t]);
            }
            for (int t = 0; t < enderChestItems.length; t++) {
                itemStackYaml.set(PATH_PREFIX_ENDER_CHEST + t, enderChestItems[t]);
            }
            itemStackYaml.set(PATH_OFF_HAND, itemOffhand);
        }

        return itemStackYaml.saveToString();
    }

    public static ItemStackOut textToItemStack(final String itemStackStr) {
        final YamlConfiguration itemStackYaml;

        try (final Reader reader = new StringReader(itemStackStr)) {
            itemStackYaml = YamlConfiguration.loadConfiguration(reader);
        } catch (final IOException e) {
            // This error should never happend!
            throw new SecuboidRuntimeException(e);
        }

        final ItemStack[] slotItems = new ItemStack[PlayerInvEntry.INVENTORY_LIST_SIZE];
        for (int t = 0; t < PlayerInvEntry.INVENTORY_LIST_SIZE; t++) {
            slotItems[t] = itemStackYaml.getItemStack(PATH_PREFIX_SLOT + t, new ItemStack(Material.AIR));
        }
        final ItemStack[] armorItems = new ItemStack[PlayerInvEntry.ARMOR_SIZE];
        for (int t = 0; t < PlayerInvEntry.ARMOR_SIZE; t++) {
            armorItems[t] = itemStackYaml.getItemStack(PATH_PREFIX_ARMOR + t, new ItemStack(Material.AIR));
        }
        final ItemStack[] enderChestItems = new ItemStack[PlayerInvEntry.ENDER_CHEST_SIZE];
        for (int t = 0; t < PlayerInvEntry.ENDER_CHEST_SIZE; t++) {
            armorItems[t] = itemStackYaml.getItemStack(PATH_PREFIX_ENDER_CHEST + t, new ItemStack(Material.AIR));
        }
        final ItemStack itemOffhand = itemStackYaml.getItemStack(PATH_OFF_HAND, new ItemStack(Material.AIR));

        return new ItemStackOut(slotItems, armorItems, enderChestItems, itemOffhand);
    }

    public static final class ItemStackOut {
        public final ItemStack[] slotItems;
        public final ItemStack[] armorItems;
        public final ItemStack[] enderChestItems;
        public final ItemStack itemOffhand;

        private ItemStackOut(final ItemStack[] slotItems, final ItemStack[] armorItems,
                final ItemStack[] enderChestItems, final ItemStack itemOffhand) {
            this.slotItems = slotItems;
            this.armorItems = armorItems;
            this.enderChestItems = enderChestItems;
            this.itemOffhand = itemOffhand;
        }
    }
}
