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
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.storage.Savable;

/**
 * The class for inventory entries.
 */
public class PlayerInvEntry implements Savable {

    private final static int MAX_FOOD_LEVEL = 20;

    private final Player player;
    private final InventorySpec actualInv;
    private final boolean isCreativeInv;
    private int level;
    private float exp;
    private double healt;
    private int foodLevel;
    private ItemStack[] itemListLoad;
    private ItemStack[] itemArmorLoad;
    private ItemStack[] itemEnderChest;
    private ItemStack itemOffhand;

    // TODO Envoyer les contents length en paramètre
    public PlayerInvEntry(final Player player, final InventorySpec actualInv, final boolean isCreativeInv) {
        this.player = player;
        this.actualInv = actualInv;
        this.isCreativeInv = isCreativeInv;
    }

    public PlayerInvEntry setDefault() {
        level = 0;
        exp = 0f;
        healt = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        foodLevel = MAX_FOOD_LEVEL;
        itemListLoad = new ItemStack[player.getInventory().getContents().length];
        resetItemStacks(itemListLoad);
        itemArmorLoad = new ItemStack[player.getInventory().getArmorContents().length];
        resetItemStacks(itemArmorLoad);
        itemEnderChest = new ItemStack[player.getEnderChest().getContents().length];
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

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }
}
