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


import me.tabinol.secuboid.storage.Savable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The class for inventory entries.
 */
public class PlayerInvEntry implements Savable {

    public final static int MAX_FOOD_LEVEL = 20;
    public final static double MAX_HEALTH = 20d;
    public final static int INVENTORY_LIST_SIZE = 41;
    public final static int ENDER_CHEST_SIZE = 27;

    private final PlayerInventoryCache playerInventoryCacheNullable;
    private final InventorySpec inventorySpec;
    private final boolean isCreativeInv;
    private final ItemStack[] slotItems;
    private final ItemStack[] enderChestItems;
    private int level;
    private float exp;
    private double health;
    private int foodLevel;
    private final List<PotionEffect> potionEffects;

    public PlayerInvEntry(final PlayerInventoryCache playerInventoryCacheNullable,
                          final InventorySpec inventorySpec, final boolean isCreativeInv) {
        this.playerInventoryCacheNullable = playerInventoryCacheNullable;
        this.inventorySpec = inventorySpec;
        this.isCreativeInv = isCreativeInv;

        slotItems = new ItemStack[INVENTORY_LIST_SIZE];
        enderChestItems = new ItemStack[ENDER_CHEST_SIZE];
        potionEffects = new ArrayList<>();
    }

    public PlayerInvEntry setDefault() {
        level = 0;
        exp = 0f;
        health = MAX_HEALTH;
        foodLevel = MAX_FOOD_LEVEL;
        resetItemStacks(slotItems);
        resetItemStacks(enderChestItems);
        return this;
    }

    private void resetItemStacks(final ItemStack[] itemStacks) {
        Arrays.fill(itemStacks, null);
    }

    PlayerInventoryCache getPlayerInventoryCacheNullable() {
        return playerInventoryCacheNullable;
    }

    public UUID getPlayerUUIDNullable() {
        return playerInventoryCacheNullable != null ? playerInventoryCacheNullable.getUUID() : null;
    }

    public InventorySpec getInventorySpec() {
        return inventorySpec;
    }

    public boolean isCreativeInv() {
        return isCreativeInv;
    }

    public ItemStack[] getSlotItems() {
        return this.slotItems;
    }

    public PlayerInvEntry setSlotItems(final ItemStack[] slotItems) {
        System.arraycopy(slotItems, 0, this.slotItems, 0, slotItems.length);
        return this;
    }

    public ItemStack[] getEnderChestItems() {
        return this.enderChestItems;
    }

    public PlayerInvEntry setEnderChestItems(final ItemStack[] enderChestItems) {
        System.arraycopy(enderChestItems, 0, this.enderChestItems, 0, enderChestItems.length);
        return this;
    }

    public int getLevel() {
        return this.level;
    }

    public PlayerInvEntry setLevel(final int level) {
        this.level = level;
        return this;
    }

    public float getExp() {
        return this.exp;
    }

    public PlayerInvEntry setExp(final float exp) {
        this.exp = exp;
        return this;
    }

    public double getHealth() {
        return this.health;
    }

    public PlayerInvEntry setHealth(final double health) {
        // #50 Fix health greater than 20
        this.health = Math.min(health, MAX_HEALTH);
        return this;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public PlayerInvEntry setFoodLevel(final int foodLevel) {
        this.foodLevel = foodLevel;
        return this;
    }

    public List<PotionEffect> getPotionEffects() {
        return Collections.unmodifiableList(potionEffects);
    }

    public PlayerInvEntry addPotionEffect(final PotionEffect potionEffect) {
        potionEffects.add(potionEffect);
        return this;
    }

    public void removePotionEffects() {
        potionEffects.clear();
    }

    @Override
    public String getName() {
        return String.format("[invName=%s, isCreativeInv=%s, playerUUID=%s]", inventorySpec.getInventoryName(),
                isCreativeInv, getPlayerUUIDNullable());
    }

    @Override
    public UUID getUUID() {
        return getPlayerUUIDNullable();
    }

    public PlayerInvEntry copyOf() {
        final PlayerInvEntry invEntryCopy = new PlayerInvEntry(playerInventoryCacheNullable, inventorySpec, isCreativeInv);
        invEntryCopy.setSlotItems(slotItems);
        invEntryCopy.setEnderChestItems(enderChestItems);
        invEntryCopy.level = level;
        invEntryCopy.exp = exp;
        invEntryCopy.health = health;
        invEntryCopy.foodLevel = foodLevel;
        for (final PotionEffect potionEffect : potionEffects) {
            invEntryCopy.addPotionEffect(potionEffect);
        }

        return invEntryCopy;
    }
}
