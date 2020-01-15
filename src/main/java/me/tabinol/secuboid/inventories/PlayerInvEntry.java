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

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.storage.Savable;

/**
 * The class for inventory entries.
 */
public class PlayerInvEntry implements Savable {

    private final static int MAX_FOOD_LEVEL = 20;
    private final static double MAX_HEALT = 20d;
    private final static int INVENTORY_LIST_SIZE = 27;
    private final static int ARMOR_SIZE = 4;
    private final static int ENDER_CHEST_SIZE = 27;

    private final Optional<PlayerConfEntry> playerConfEntryOpt;
    private final InventorySpec actualInv;
    private final boolean isCreativeInv;
    final private ItemStack[] itemListLoad;
    final private ItemStack[] itemArmorLoad;
    final private ItemStack[] itemEnderChest;
    private int level;
    private float exp;
    private double healt;
    private int foodLevel;
    private ItemStack itemOffhand;

    public PlayerInvEntry(final Optional<PlayerConfEntry> playerConfEntryOpt, final InventorySpec actualInv,
            final boolean isCreativeInv) {
        this.playerConfEntryOpt = playerConfEntryOpt;
        this.actualInv = actualInv;
        this.isCreativeInv = isCreativeInv;
        itemListLoad = new ItemStack[INVENTORY_LIST_SIZE];
        itemArmorLoad = new ItemStack[ARMOR_SIZE];
        itemEnderChest = new ItemStack[ENDER_CHEST_SIZE];
    }

    public PlayerInvEntry setDefault() {
        level = 0;
        exp = 0f;
        healt = MAX_HEALT;
        foodLevel = MAX_FOOD_LEVEL;
        resetItemStacks(itemListLoad);
        resetItemStacks(itemArmorLoad);
        resetItemStacks(itemEnderChest);
        itemOffhand = new ItemStack(Material.AIR);
        return this;
    }

    private void resetItemStacks(final ItemStack[] itemStacks) {
        Arrays.stream(itemStacks).forEach(itemStack -> itemStack = new ItemStack(Material.AIR));
    }

    public Optional<PlayerConfEntry> getPlayerConfEntryOpt() {
        return playerConfEntryOpt;
    }

    public PlayerInvEntry setDefault() {
        level = 0;
        exp = 0f;
        healt = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        foodLevel = MAX_FOOD_LEVEL;
        resetItemStacks(itemListLoad);
        resetItemStacks(itemArmorLoad);
        resetItemStacks(itemEnderChest);
        itemOffhand = new ItemStack(Material.AIR);
        return this;
    }

    private void resetItemStacks(final ItemStack[] itemStacks) {
        Arrays.stream(itemStacks).forEach(itemStack -> itemStack = new ItemStack(Material.AIR));
    }

    public Player getPlayer() {
        return player;
    }

    public InventorySpec getActualInv() {
        return actualInv;
    }

    public boolean isCreativeInv() {
        return isCreativeInv;
    }

    public ItemStack[] getItemListLoad() {
        return this.itemListLoad;
    }

    public ItemStack[] getItemArmorLoad() {
        return this.itemArmorLoad;
    }

    public ItemStack[] getItemEnderChest() {
        return this.itemEnderChest;
    }

    public int getLevel() {
        return this.level;
    }

    public PlayerInvEntry setLevel(int level) {
        this.level = level;
        return this;
    }

    public float getExp() {
        return this.exp;
    }

    public PlayerInvEntry setExp(float exp) {
        this.exp = exp;
        return this;
    }

    public double getHealt() {
        return this.healt;
    }

    public PlayerInvEntry setHealt(double healt) {
        this.healt = healt;
        return this;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public PlayerInvEntry setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
        return this;
    }

    public ItemStack getItemOffhand() {
        return this.itemOffhand;
    }

    public PlayerInvEntry setItemOffhand(ItemStack itemOffhand) {
        this.itemOffhand = itemOffhand;
        return this;
    }

    @Override
    public String getName() {
        return String.format("[invName=%s, isCreativeInv=%s, playerName=%s]", actualInv.getInventoryName(),
                isCreativeInv, playerConfEntryOpt.map(pce -> pce.getName()).orElse(null));
    }

    @Override
    public UUID getUUID() {
        return playerConfEntryOpt.map(pce -> pce.getUUID()).orElse(null);
    }
}
