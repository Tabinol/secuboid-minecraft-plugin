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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;

/**
 * Inventories store for default inventories. Player inventories are in
 * PlayerConfig.
 */
public final class Inventories {

    private final Secuboid secuboid;
    private final Map<String, PlayerInvEntry> InventoryNameToDefaultInvEntry;

    /**
     * Player Join, Quit, Change
     */
    public enum PlayerAction {
        JOIN, QUIT, CHANGE, DEATH
    }

    public Inventories(Secuboid secuboid) {
        this.secuboid = secuboid;
        InventoryNameToDefaultInvEntry = new HashMap<>();
    }

    public void saveInventory(final Player player, final boolean isDeath, final PlayerInvEntry playerInvEntry, final boolean isSaveAllowed,
            final boolean isDefaultInv, final boolean isEnderChestOnly) {

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (!isSaveAllowed) {
            return;
        }

        // Update playerInvEntry from player (if there is a player)
        updateFromPlayer(player, playerInvEntry, isEnderChestOnly);

        // Request save
        if (isDeath) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_DEATH_HISTORY_SAVE, Optional.of(playerInvEntry));
        } else if (isDefaultInv) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_DEFAULT_SAVE, Optional.of(playerInvEntry));
        } else if (isEnderChestOnly) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_DEATH_SAVE, Optional.of(playerInvEntry));
        } else {
            // Normal save
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.INVENTORY_PLAYER_SAVE, Optional.of(playerInvEntry));
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

    public boolean loadInventory(final Player player, final String invName, final boolean isCreative,
            final boolean fromDeath, final int deathVersion) {

        boolean invExist = false;
        String suffixName;

        // Get the suffix name
        final String gmName = InventoryType.getFromBoolean(isCreative).name();
        if (fromDeath) {
            suffixName = gmName + "." + DEATH + "." + deathVersion;
        } else {
            suffixName = gmName;
        }

        final YamlConfiguration ConfigPlayerItemFile = new YamlConfiguration();

        // player item file
        File playerItemFile = new File(secuboid.getDataFolder() + "/" + INV_DIR + "/" + invName + "/"
                + player.getUniqueId().toString() + "." + suffixName + ".yml");

        if (!fromDeath && !playerItemFile.exists()) {

            // Check for default inventory file
            playerItemFile = new File(
                    secuboid.getDataFolder() + "/" + INV_DIR + "/" + invName + "/" + DEFAULT_INV + ".yml");
        }

        if (playerItemFile.exists()) {

            invExist = true;

            try {

                // load Inventory
                ConfigPlayerItemFile.load(playerItemFile);

                @SuppressWarnings("unused")
                final int version = ConfigPlayerItemFile.getInt("Version");

                // player.setTotalExperience(ConfigPlayerItemFile.getInt("Experience"));
                player.setLevel(ConfigPlayerItemFile.getInt("Level"));
                player.setExp((float) ConfigPlayerItemFile.getDouble("Exp"));

                if (!fromDeath) {
                    final double healt = ConfigPlayerItemFile.getDouble("Health");
                    if (healt > 0) {
                        player.setHealth(healt);
                        player.setFoodLevel(ConfigPlayerItemFile.getInt("FoodLevel"));
                    } else {
                        // Fix Death infinite loop
                        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                        player.setFoodLevel(MAX_FOOD_LEVEL);
                    }
                }

                final ItemStack[] itemListLoad = new ItemStack[player.getInventory().getContents().length];
                final ItemStack[] itemArmorLoad = new ItemStack[player.getInventory().getArmorContents().length];
                final ItemStack[] itemEnderChest = new ItemStack[player.getEnderChest().getContents().length];
                for (int t = 0; t < itemListLoad.length; t++) {
                    itemListLoad[t] = ConfigPlayerItemFile.getItemStack("Slot." + t, new ItemStack(Material.AIR));
                }
                for (int t = 0; t < itemArmorLoad.length; t++) {
                    itemArmorLoad[t] = ConfigPlayerItemFile.getItemStack("Armor." + t, new ItemStack(Material.AIR));
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    itemEnderChest[t] = ConfigPlayerItemFile.getItemStack("EnderChest." + t,
                            new ItemStack(Material.AIR));
                }
                final ItemStack itemOffhand = ConfigPlayerItemFile.getItemStack("OffHand.0",
                        new ItemStack(Material.AIR));

                player.getInventory().setContents(itemListLoad);
                player.getInventory().setArmorContents(itemArmorLoad);
                player.getEnderChest().setContents(itemEnderChest);
                player.getInventory().setItemInOffHand(itemOffhand);

                // PotionsEffects
                removePotionEffects(player);
                final ConfigurationSection effectSection = ConfigPlayerItemFile.getConfigurationSection("PotionEffect");
                if (effectSection != null) {
                    for (final Map.Entry<String, Object> effectEntry : effectSection.getValues(false).entrySet()) {

                        final PotionEffectType type = PotionEffectType.getByName(effectEntry.getKey());
                        final ConfigurationSection effectSubSection = (ConfigurationSection) effectEntry.getValue();
                        final int duration = effectSubSection.getInt("Duration");
                        final int amplifier = effectSubSection.getInt("Amplifier");
                        final boolean ambient = effectSubSection.getBoolean("Ambient");
                        player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient), true);
                    }
                }

            } catch (final IOException ex) {
                secuboid.getLogger().severe("Error on inventory load for player " + player.getName() + ", filename: "
                        + playerItemFile.getPath());
            } catch (final InvalidConfigurationException ex) {
                secuboid.getLogger().severe("Invalid configuration on inventory load for player " + player.getName()
                        + ", filename: " + playerItemFile.getPath());
            }
        } else if (!fromDeath) {

            // The file is not existing, only clear all inventory
            player.setLevel(0);
            player.setExp(0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            player.setFoodLevel(MAX_FOOD_LEVEL);
            player.getInventory().clear();
            player.getInventory().setBoots(new ItemStack(Material.AIR));
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
            player.getInventory().setLeggings(new ItemStack(Material.AIR));
            player.getEnderChest().clear();
            removePotionEffects(player);
        }

        return invExist;

    }
}