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
package me.tabinol.secuboid.storage.flat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.Inventories;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;

/**
 * InventoriesFlat
 */
public class InventoriesFlat {

    private final static String INV_DIR = "inventories";
    private final static String DEFAULT_INV = "DEFAULTINV";
    private final static String DEATH = "DEATH";
    private final static String INV_EXT = ".yml";

    private final Secuboid secuboid;
    private final int storageVersion;

    public InventoriesFlat(final Secuboid secuboid) {
        this.secuboid = secuboid;
        storageVersion = secuboid.getMavenAppProperties().getPropertyInt("inventoryStorageVersion");
    }

    public void loadInventories() {
        final Inventories inventories = secuboid.getInventoriesOpt().get();
        for (final InventorySpec inventorySpec : inventories.getInvSpecs()) {
            final File invDirFile = getInventoryDir(inventorySpec.getInventoryName());
            if (invDirFile.isDirectory()) {
                final File invDefaultFile = new File(invDirFile, DEFAULT_INV + INV_EXT);
                if (invDefaultFile.isFile()) {
                    final PlayerInvEntry playerInvEntry = loadInventoryFromFile(invDefaultFile, Optional.empty(),
                            inventorySpec, false);
                    inventories.addDefaultInventory(playerInvEntry);
                }
            }
        }
    }

    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUuidOpt(), false, false);

    }

    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        final File invDirFile = getInventoryDir(playerInvEntry.getInventorySpec().getInventoryName());
        final File invFile = new File(invDirFile, DEFAULT_INV + INV_EXT);

        if (!invFile.delete()) {
            secuboid.getLogger().log(Level.WARNING,
                    String.format("Unable to delete the inventory, filename: %s", invFile.getPath()));
        }
    }

    public void loadInventoriesPlayer(final PlayerInventoryCache playerInventoryCache) {
        final Inventories inventories = secuboid.getInventoriesOpt().get();
        final UUID playerUuid = playerInventoryCache.getUUID();
        final Thread preLoginThread = secuboid.getStorageThread().preloginRemoveThread(playerUuid);

        for (final InventorySpec inventorySpec : inventories.getInvSpecs()) {
            final File invDir = getInventoryDir(inventorySpec.getInventoryName());

            // Survival
            final File survivalFile = new File(invDir,
                    String.format("%s.%s%s", playerUuid, getGameModeFromBoolean(false), INV_EXT));
            if (survivalFile.exists()) {
                final PlayerInvEntry playerInvEntry = loadInventoryFromFile(survivalFile, Optional.of(playerUuid),
                        inventorySpec, false);
                playerInventoryCache.addInventorySurvival(inventorySpec, playerInvEntry);
            }

            // Creative
            final File creativeFile = new File(invDir,
                    String.format("%s.%s%s", playerUuid, getGameModeFromBoolean(true), INV_EXT));
            if (creativeFile.exists()) {
                final PlayerInvEntry playerInvEntry = loadInventoryFromFile(creativeFile, Optional.of(playerUuid),
                        inventorySpec, true);
                playerInventoryCache.addInventorySurvival(inventorySpec, playerInvEntry);
            }

            // Death
            for (int deathVersion = 1; deathVersion <= PlayerInventoryCache.DEATH_SAVE_MAX_NBR; deathVersion++) {
                final File deathFile = new File(invDir, String.format("%s.%s.%s.%s%s", playerUuid,
                        getGameModeFromBoolean(true), DEATH, deathVersion, INV_EXT));
                if (deathFile.exists()) {
                    final PlayerInvEntry playerInvEntry = loadInventoryFromFile(deathFile, Optional.of(playerUuid),
                            inventorySpec, false);
                    playerInventoryCache.addInventoryDeath(deathVersion, playerInvEntry);
                }
            }
        }

        // Notify pre login thread
        synchronized (preLoginThread) {
            preLoginThread.notify();
        }
    }

    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUuidOpt(), false, false);

    }

    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUuidOpt(), false, true);
    }

    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUuidOpt(), true, false);
    }

    private PlayerInvEntry loadInventoryFromFile(final File playerItemFile, final Optional<UUID> playerUuidOpt,
            final InventorySpec inventorySpec, final boolean isCreative) {
        final YamlConfiguration configPlayerItemFile = new YamlConfiguration();
        final PlayerInvEntry playerInvEntry = new PlayerInvEntry(playerUuidOpt, inventorySpec, isCreative);

        try {
            // load Inventory
            configPlayerItemFile.load(playerItemFile);

            @SuppressWarnings("unused")
            final int version = configPlayerItemFile.getInt("Version");

            playerInvEntry.setLevel(configPlayerItemFile.getInt("Level"));
            playerInvEntry.setExp((float) configPlayerItemFile.getDouble("Exp"));

            playerInvEntry.setHealth(configPlayerItemFile.getDouble("Health"));
            playerInvEntry.setFoodLevel(configPlayerItemFile.getInt("FoodLevel"));

            final ItemStack[] itemListLoad = playerInvEntry.getSlotItems();
            final ItemStack[] itemArmorLoad = playerInvEntry.getArmorItems();
            final ItemStack[] itemEnderChest = playerInvEntry.getEnderChestItems();
            for (int t = 0; t < itemListLoad.length; t++) {
                itemListLoad[t] = configPlayerItemFile.getItemStack("Slot." + t, new ItemStack(Material.AIR));
            }
            for (int t = 0; t < itemArmorLoad.length; t++) {
                itemArmorLoad[t] = configPlayerItemFile.getItemStack("Armor." + t, new ItemStack(Material.AIR));
            }
            for (int t = 0; t < itemEnderChest.length; t++) {
                itemEnderChest[t] = configPlayerItemFile.getItemStack("EnderChest." + t, new ItemStack(Material.AIR));
            }
            playerInvEntry.setItemOffhand(configPlayerItemFile.getItemStack("OffHand.0", new ItemStack(Material.AIR)));

            // PotionsEffects
            final ConfigurationSection effectSection = configPlayerItemFile.getConfigurationSection("PotionEffect");
            if (effectSection != null) {
                for (final Map.Entry<String, Object> effectEntry : effectSection.getValues(false).entrySet()) {

                    final PotionEffectType type = PotionEffectType.getByName(effectEntry.getKey());
                    final ConfigurationSection effectSubSection = (ConfigurationSection) effectEntry.getValue();
                    final int duration = effectSubSection.getInt("Duration");
                    final int amplifier = effectSubSection.getInt("Amplifier");
                    final boolean ambient = effectSubSection.getBoolean("Ambient");
                    playerInvEntry.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient));
                }
            }

        } catch (final IOException ex) {
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Error in inventory load, filename: %s", playerItemFile.getPath()), ex);
            playerInvEntry.setDefault();
        } catch (final InvalidConfigurationException ex) {
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Invalid configuration on inventory load, filename: %s", playerItemFile.getPath()),
                    ex);
            playerInvEntry.setDefault();
        }

        return playerInvEntry;
    }

    public void saveInventory(final PlayerInvEntry playerInvEntry, final Optional<UUID> playerUuidOpt,
            final boolean isDeathHistory, final boolean enderChestOnly) {
        final InventorySpec inventorySpec = playerInvEntry.getInventorySpec();

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (!playerUuidOpt.isPresent() && !inventorySpec.isSaveInventory()) {
            return;
        }

        // Create directories (if not here)
        final File invDirFile = getInventoryDir(inventorySpec.getInventoryName());
        invDirFile.mkdirs();
        if (!invDirFile.isDirectory()) {
            secuboid.getLogger().severe("Unable to create the directory: " + invDirFile.getPath());
        }

        // Get the suffix name
        final String gmName = getGameModeFromBoolean(playerInvEntry.isCreativeInv());
        final String filePreName;

        if (isDeathHistory && playerUuidOpt.isPresent()) {
            // Save death inventory
            final String playerUUIDStr = playerUuidOpt.get().toString();
            final String fileDeathPrefix = playerUUIDStr + "." + gmName + "." + DEATH + ".";
            filePreName = fileDeathPrefix + "1";

            // Death rename
            File actFile = new File(invDirFile, fileDeathPrefix + "9" + INV_EXT);
            if (actFile.exists()) {
                if (!actFile.delete()) {
                    secuboid.getLogger().severe("Unable to delete the file: " + actFile.getPath());
                }
            }
            for (int t = 8; t >= 1; t--) {
                actFile = new File(invDirFile, fileDeathPrefix + t + INV_EXT);
                if (actFile.exists()) {
                    if (!actFile.renameTo(new File(invDirFile, fileDeathPrefix + (t + 1) + INV_EXT))) {
                        secuboid.getLogger().severe("Unable to rename the file: " + actFile.getPath());
                    }
                }
            }

        } else if (!playerUuidOpt.isPresent()) {
            // Save default inventory
            filePreName = DEFAULT_INV;

        } else {
            // Save normal inventory
            filePreName = playerUuidOpt.get() + "." + gmName;
        }

        // Save Inventory
        final YamlConfiguration configPlayerItemFile = new YamlConfiguration();
        final File playerItemFile = new File(invDirFile, filePreName + INV_EXT);

        try {
            configPlayerItemFile.set("Version", storageVersion);

            // Save Only ender chest (Death)
            if (enderChestOnly) {
                configPlayerItemFile.set("Level", 0);
                configPlayerItemFile.set("Exp", 0f);
                configPlayerItemFile.set("Health", PlayerInvEntry.MAX_HEALTH);
                configPlayerItemFile.set("FoodLevel", PlayerInvEntry.MAX_FOOD_LEVEL);

                final ItemStack[] itemEnderChest = playerInvEntry.getEnderChestItems();
                for (int t = 0; t < 4; t++) {
                    configPlayerItemFile.set("Armor." + t, new ItemStack(Material.AIR));
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    configPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
                }
            } else {
                // Save all
                configPlayerItemFile.set("Level", playerInvEntry.getLevel());
                configPlayerItemFile.set("Exp", playerInvEntry.getExp());
                configPlayerItemFile.set("Health", playerInvEntry.getHealth());
                configPlayerItemFile.set("FoodLevel", playerInvEntry.getFoodLevel());

                final ItemStack[] itemListSave = playerInvEntry.getSlotItems();
                final ItemStack[] itemArmorSave = playerInvEntry.getArmorItems();
                final ItemStack[] itemEnderChest = playerInvEntry.getEnderChestItems();
                final ItemStack itemOffhand = playerInvEntry.getItemOffhand();
                for (int t = 0; t < itemListSave.length; t++) {
                    configPlayerItemFile.set("Slot." + t, itemListSave[t]);
                }
                for (int t = 0; t < itemArmorSave.length; t++) {
                    configPlayerItemFile.set("Armor." + t, itemArmorSave[t]);
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    configPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
                }
                configPlayerItemFile.set("OffHand.0", itemOffhand);

                // PotionsEffects
                final List<PotionEffect> activePotionEffects = playerInvEntry.getPotionEffects();
                final ConfigurationSection effectSection = configPlayerItemFile.createSection("PotionEffect");
                for (final PotionEffect effect : activePotionEffects) {
                    final ConfigurationSection effectSubSection = effectSection
                            .createSection(effect.getType().getName());
                    effectSubSection.set("Duration", effect.getDuration());
                    effectSubSection.set("Amplifier", effect.getAmplifier());
                    effectSubSection.set("Ambient", effect.isAmbient());
                }
            }

            configPlayerItemFile.save(playerItemFile);

        } catch (final IOException ex) {
            secuboid.getLogger().severe("Error on inventory save, filename: " + playerItemFile.getPath());
        }
    }

    private File getInventoryDir(final String invName) {
        return new File(secuboid.getDataFolder(), INV_DIR + "/" + invName);
    }

    private String getGameModeFromBoolean(final boolean isCreative) {
        return isCreative ? "CREATIVE" : "SURVIVAL";
    }
}