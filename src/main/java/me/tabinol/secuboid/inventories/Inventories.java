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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;

/**
 * Inventories store for default inventories. Player inventories are in
 * PlayerConfig.
 */
public final class Inventories {

    private final Secuboid secuboid;
    private final InventoryConfig inventoryConfig;
    private final Map<InventorySpec, PlayerInvEntry> inventorySpecToDefaultInvEntry;

    /**
     * Player Join, Quit, Change
     */
    public enum PlayerAction {
        JOIN, QUIT, CHANGE, DEATH
    }

    public Inventories(final Secuboid secuboid, final InventoryConfig inventoryConfig) {
        this.secuboid = secuboid;
        this.inventoryConfig = inventoryConfig;
        inventorySpecToDefaultInvEntry = new HashMap<>();
    }

    public void reloadConfig() {
        inventoryConfig.reloadConfig();
    }

    public Collection<InventorySpec> getInvSpecs() {
        return inventoryConfig.getInvSpecs();
    }

    public boolean loadDeathInventory(final Player player, final int deathVersion) {
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
        final InventorySpec invSpec = playerConfEntry.getPlayerInventoryCacheOpt().get().getCurInvEntry()
                .getInventorySpec();
        return loadInventoryToPlayer(playerConfEntry, invSpec, player.getGameMode() == GameMode.CREATIVE, true,
                deathVersion);
    }

    public void addDefaultInventory(final PlayerInvEntry playerInvEntry) {
        inventorySpecToDefaultInvEntry.put(playerInvEntry.getInventorySpec(), playerInvEntry);
    }

    public void saveDefaultInventory(final PlayerConfEntry playerConfEntry) {
        final Player player = playerConfEntry.getPlayer();
        saveInventory(player, playerConfEntry.getPlayerInventoryCacheOpt().get().getCurInvEntry(), false, true, false);
    }

    /**
     * Called when there is a shutdown
     */
    public void removeAndSave() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            removePlayer(player);
        }
    }

    public void forceSave() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final PlayerInvEntry playerInvEntry = secuboid.getPlayerConf().get(player).getPlayerInventoryCacheOpt()
                    .get().getCurInvEntry();
            if (!player.isDead()) {
                saveInventory(player, playerInvEntry, false, false, false);
            }
        }
    }

    public void removePlayer(final Player player) {
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(player.getLocation());
        switchInventory(playerConfEntry, landPermissionsFlags, player.getGameMode() == GameMode.CREATIVE,
                PlayerAction.QUIT);
    }

    public void saveInventory(final Player player, final PlayerInvEntry playerInvEntry, final boolean isDeath,
            final boolean isDefaultInv, final boolean isEnderChestOnly) {

        // Il a player is just connected before the inventory load
        if (playerInvEntry == null) {
            return;
        }

        final InventorySpec inventorySpec = playerInvEntry.getInventorySpec();

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (!inventorySpec.isSaveInventory() && !isDefaultInv) {
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

    private void updateFromPlayer(final Player player, final PlayerInvEntry playerInvEntry,
            final boolean isEnderChestOnly) {
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

    private void replaceAllInArray(final ItemStack[] source, final ItemStack[] target) {
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }
    }

    private void replaceAllPotionEffects(final Player sourcePlayer, final PlayerInvEntry targetPlayerInvEntry) {
        targetPlayerInvEntry.removePotionEffects();
        for (final PotionEffect potionEffect : sourcePlayer.getActivePotionEffects()) {
            targetPlayerInvEntry.addPotionEffect(potionEffect);
        }
    }

    public boolean loadInventoryToPlayer(final PlayerConfEntry playerConfEntry, final InventorySpec inventorySpec,
            final boolean isCreative, final boolean fromDeath, final int deathVersion) {
        final Player player = playerConfEntry.getPlayer();
        final PlayerInventoryCache playerInventoryCache = playerConfEntry.getPlayerInventoryCacheOpt().get();

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

    private void replaceAllPotionEffectsToPlayer(final PlayerInvEntry sourcePlayerInvEntry, final Player targetPlayer) {
        removePotionEffects(targetPlayer);
        for (final PotionEffect potionEffect : sourcePlayerInvEntry.getPotionEffects()) {
            targetPlayer.addPotionEffect(potionEffect, true);
        }
    }

    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        inventorySpecToDefaultInvEntry.remove(playerInvEntry.getInventorySpec());
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_DEFAULT_REMOVE, Optional.of(playerInvEntry));
    }

    public void switchInventory(final PlayerConfEntry playerConfEntry, final LandPermissionsFlags landPermissionsFlags,
            boolean toIsCreative, final PlayerAction playerAction) {
        final Player player = playerConfEntry.getPlayer();
        final PlayerInventoryCache playerInventoryCache = playerConfEntry.getPlayerInventoryCacheOpt().get();

        PlayerInvEntry fromInvEntry = null;
        boolean fromIsCreative = false;
        InventorySpec fromInv = null;
        InventorySpec toInv;

        // Check last values
        if (playerAction != PlayerAction.JOIN) {
            fromInvEntry = playerInventoryCache.getCurInvEntry();
        }

        // invEntry is null if the player is new
        if (fromInvEntry != null) {
            fromIsCreative = fromInvEntry.isCreativeInv();
            fromInv = fromInvEntry.getInventorySpec();
        }

        // Get new inventory
        toInv = inventoryConfig.getInvSpec(landPermissionsFlags);

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
            playerInventoryCache
                    .setCurInvEntry(new PlayerInvEntry(Optional.of(player.getUniqueId()), toInv, toIsCreative));
        }

        // Return if the inventory will be exacly the same
        if (playerAction != PlayerAction.DEATH && playerAction != PlayerAction.QUIT && (fromInv != null
                && fromInv.getInventoryName().equals(toInv.getInventoryName()) && fromIsCreative == toIsCreative)) {
            return;
        }

        // If the player is death, save a renamed file
        if (playerAction == PlayerAction.DEATH && fromInvEntry != null) {
            saveInventory(player, fromInvEntry, true, false, false);
        }

        // Save last inventory (only EnderChest if death)
        if (playerAction != PlayerAction.JOIN && fromInvEntry != null) {
            saveInventory(player, fromInvEntry, false, false, playerAction == PlayerAction.DEATH);
        }

        // Don't load a new inventory if the player quit
        if (playerAction != PlayerAction.QUIT && playerAction != PlayerAction.DEATH) {
            loadInventoryToPlayer(playerConfEntry, toInv, toIsCreative, false, 0);
        }
    }
}