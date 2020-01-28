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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;

/**
 * Inventories store for default inventories. Player inventories are in
 * PlayerConfig.
 */
public final class Inventories {

    private final Secuboid secuboid;
    private final Map<InventorySpec, PlayerInvEntry> inventorySpecToDefaultInvEntry;

    /**
     * Player Join, Quit, Change
     */
    public enum PlayerAction {
        JOIN, QUIT, CHANGE, DEATH
    }

    public Inventories(Secuboid secuboid) {
        this.secuboid = secuboid;
        inventorySpecToDefaultInvEntry = new HashMap<>();
    }

    public void saveInventory(final Player player, final boolean isDeath, final PlayerInvEntry playerInvEntry,
            final boolean isSaveAllowed, final boolean isDefaultInv, final boolean isEnderChestOnly) {

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (!isSaveAllowed) {
            return;
        }

        // Update playerInvEntry from player (if there is a player)
        updateFromPlayer(player, playerInvEntry, isEnderChestOnly);

        // Request save
        if (isDeath) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_DEATH_HISTORY_SAVE,
                    Optional.of(playerInvEntry));
        } else if (isDefaultInv) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_DEFAULT_SAVE,
                    Optional.of(playerInvEntry));
        } else if (isEnderChestOnly) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_DEATH_SAVE,
                    Optional.of(playerInvEntry));
        } else {
            // Normal save
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_SAVE,
                    Optional.of(playerInvEntry));
        }
    }

    private void updateFromPlayer(Player player, PlayerInvEntry playerInvEntry, boolean isEnderChestOnly) {
        // If the player is death, reset and save only ender chest
        if (isEnderChestOnly) {
            playerInvEntry.setDefault();
            replaceAllInArray(player.getEnderChest().getStorageContents(), playerInvEntry.getEnderChestItems());
            return;
        }

        // Normal save
        replaceAllInArray(player.getInventory().getContents(), playerInvEntry.getSlotItems());
        replaceAllInArray(player.getInventory().getArmorContents(), playerInvEntry.getArmorItems());
        replaceAllInArray(player.getEnderChest().getStorageContents(), playerInvEntry.getEnderChestItems());
        playerInvEntry.setLevel(player.getLevel()) //
                .setExp(player.getExp()) //
                .setHealth(player.getHealth()) //
                .setFoodLevel(player.getFoodLevel()) //
                .setItemOffhand(player.getInventory().getItemInOffHand());
        replaceAllPotionEffects(player, playerInvEntry);

    }

    private void replaceAllInArray(ItemStack[] source, ItemStack[] target) {
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }
    }

    private void replaceAllPotionEffects(Player sourcePlayer, PlayerInvEntry targetPlayerInvEntry) {
        targetPlayerInvEntry.removePotionEffects();
        for (PotionEffect potionEffect : sourcePlayer.getActivePotionEffects()) {
            targetPlayerInvEntry.addPotionEffect(potionEffect);
        }
    }

    public boolean loadInventoryToPlayer(final PlayerConfEntry playerConfEntry, final InventorySpec inventorySpec,
            final boolean isCreative, final boolean fromDeath, final int deathVersion) {
        final Player player = playerConfEntry.getPlayer();
        final PlayerInventoryCache playerInventoryCache = playerConfEntry.getPlayerInventoryCache();

        PlayerInvEntry playerInvEntry;
        if (fromDeath) {
            // Inventory from death
            playerInvEntry = playerInventoryCache.getInventoryDeath(deathVersion);
        } else {
            // Player normal inventory
            if (isCreative) {
                playerInvEntry = playerInventoryCache.getInventoryCreative(inventorySpec);
            } else {
                playerInvEntry = playerInventoryCache.getInventorySurvival(inventorySpec);
            }

            // Check for default inventory file if not existing
            if (playerInvEntry == null) {
                playerInvEntry = inventorySpecToDefaultInvEntry.get(inventorySpec);
            }
        }

        if (playerInvEntry != null) {
            // Inventory exists
            player.setLevel(playerInvEntry.getLevel());
            player.setExp(playerInvEntry.getExp());
            if (!fromDeath) {
                final double health = playerInvEntry.getHealth();
                if (health > 0) {
                    player.setHealth(health);
                    player.setFoodLevel(playerInvEntry.getFoodLevel());
                } else {
                    // Fix Death infinite loop
                    player.setHealth(PlayerInvEntry.MAX_HEALTH);
                    player.setFoodLevel(PlayerInvEntry.MAX_FOOD_LEVEL);
                }
            }
            player.getInventory().setContents(playerInvEntry.getSlotItems());
            player.getInventory().setArmorContents(playerInvEntry.getArmorItems());
            player.getEnderChest().setContents(playerInvEntry.getEnderChestItems());
            player.getInventory().setItemInOffHand(playerInvEntry.getItemOffhand());

            // PotionsEffects
            replaceAllPotionEffectsToPlayer(playerInvEntry, player);

            return true;

        } else if (!fromDeath) {
            // The file is not existing, only clear all inventory
            player.setLevel(0);
            player.setExp(0);
            player.setHealth(PlayerInvEntry.MAX_HEALTH);
            player.setFoodLevel(PlayerInvEntry.MAX_FOOD_LEVEL);
            player.getInventory().clear();
            player.getInventory().setBoots(new ItemStack(Material.AIR));
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
            player.getInventory().setLeggings(new ItemStack(Material.AIR));
            player.getEnderChest().clear();
            removePotionEffects(player);
        }

        return false;
    }

    private void removePotionEffects(final Player player) {
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    private void replaceAllPotionEffectsToPlayer(PlayerInvEntry sourcePlayerInvEntry, Player targetPlayer) {
        removePotionEffects(targetPlayer);
        for (final PotionEffect potionEffect : sourcePlayerInvEntry.getPotionEffects()) {
            targetPlayer.addPotionEffect(potionEffect, true);
        }
    }

    public void switchInventory(final PlayerConfEntry playerConfEntry, final LandPermissionsFlags landPermissionsFlags,
            boolean toIsCreative, final PlayerAction playerAction) {
        final Player player = playerConfEntry.getPlayer();
        final PlayerInventoryCache playerInventoryCache = playerConfEntry.getPlayerInventoryCache();
        
        PlayerInvEntry invEntry = null;
        boolean fromIsCreative = false;
        InventorySpec fromInv = null;
        InventorySpec toInv;

        // Check last values
        if (playerAction != PlayerAction.JOIN) {
            invEntry = playerInvList.get(player);
        }

        // invEntry is null if the player is new
        if (invEntry != null) {
            fromIsCreative = invEntry.isCreativeInv();
            fromInv = invEntry.getInventorySpec();
        }

        // Get new inventory
        toInv = secuboid.getInventoryConf().getInvSpec(landPermissionsFlags);

        // check if we have to do this action
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_INV)) {
            return;
        }

        // Force survival value if we do not change to creative inventory
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
                || (fromInv != null && !fromInv.isCreativeChange())) {
            fromIsCreative = false;
        }
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV) || !toInv.isCreativeChange()) {
            toIsCreative = false;
        }

        // Update player inventory information
        if (playerAction != PlayerAction.QUIT) {
            // TODO Resolve and Uncomment
            //playerInvList.put(player,
            //        new PlayerInvEntry(Optional.of(player), toInv, toIsCreative));
        }

        // Return if the inventory will be exacly the same
        if (playerAction != PlayerAction.DEATH && playerAction != PlayerAction.QUIT && (fromInv != null
                && fromInv.getInventoryName().equals(toInv.getInventoryName()) && fromIsCreative == toIsCreative)) {
            return;
        }

        // If the player is death, save a renamed file
        if (playerAction == PlayerAction.DEATH && fromInv != null) {
            saveInventory(player, fromInv.getInventoryName(), fromIsCreative, true, fromInv.isSaveInventory(), false,
                    false);
        }

        // Save last inventory (only EnderChest if death)
        if (playerAction != PlayerAction.JOIN && fromInv != null) {
            saveInventory(player, fromInv.getInventoryName(), fromIsCreative, false, fromInv.isSaveInventory(), false,
                    playerAction == PlayerAction.DEATH);
        }

        // Don't load a new inventory if the player quit
        if (playerAction != PlayerAction.QUIT && playerAction != PlayerAction.DEATH) {
            loadInventory(player, toInv.getInventoryName(), toIsCreative, false, 0);
        }

        // If the player quit, update Offline Inventories and remove player
        if (playerAction == PlayerAction.QUIT) {
            playerInvList.remove(player);
        }
    }
}