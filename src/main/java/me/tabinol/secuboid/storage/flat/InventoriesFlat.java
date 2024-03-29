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
package me.tabinol.secuboid.storage.flat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

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
import me.tabinol.secuboid.utilities.FileUtil;
import me.tabinol.secuboid.utilities.MavenAppProperties;

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
        storageVersion = MavenAppProperties.getPropertyInt("inventoryStorageVersion", 1);
    }

    public void loadInventories() {
        if (!secuboid.getInventoriesOpt().isPresent()) {
            return;
        }

        // Prior 1.6.0 to lowercase
        File inventoryDir = getInventoryDir();
        File[] invDirFiles = inventoryDir.listFiles();
        if (invDirFiles == null) {
            return;
        }

        for (File invDirFile : invDirFiles) {
            if (invDirFile.isDirectory()) {
                invDirFile.renameTo(new File(invDirFile.getParentFile(), invDirFile.getName().toLowerCase()));
            }
        }

        Inventories inventories = secuboid.getInventoriesOpt().get();
        for (File invDirFile : invDirFiles) {
            if (invDirFile.isDirectory()) {
                File invDefaultFile = new File(invDirFile, DEFAULT_INV + INV_EXT);
                if (invDefaultFile.isFile()) {
                    String invName = invDirFile.getName();
                    InventorySpec inventorySpec = inventories.getOrCreateInvSpec(invName);
                    PlayerInvEntry playerInvEntry = loadInventoryFromFile(invDefaultFile, null, inventorySpec, false);
                    inventories.saveInventory(null, playerInvEntry, false, true, false);
                }
            }
        }
    }

    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, false, true);
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
        final UUID playerUUID = playerInventoryCache.getUUID();
        File inventoryDir = getInventoryDir();
        File[] invDirFiles = inventoryDir.listFiles();
        if (invDirFiles == null) {
            return;
        }

        for (File invDirFile : invDirFiles) {
            if (invDirFile.isDirectory()) {
                String invName = invDirFile.getName();
                InventorySpec inventorySpec = inventories.getOrCreateInvSpec(invName);
                final File invDir = getInventoryDir(inventorySpec.getInventoryName());

                // Survival
                final File survivalFile = new File(invDir,
                        String.format("%s.%s%s", playerUUID, getGameModeFromBoolean(false), INV_EXT));
                if (survivalFile.exists()) {
                    final PlayerInvEntry playerInvEntry = loadInventoryFromFile(survivalFile, playerInventoryCache,
                            inventorySpec, false);
                    inventories.saveInventory(null, playerInvEntry, false, false, false);
                }

                // Creative
                final File creativeFile = new File(invDir,
                        String.format("%s.%s%s", playerUUID, getGameModeFromBoolean(true), INV_EXT));
                if (creativeFile.exists()) {
                    final PlayerInvEntry playerInvEntry = loadInventoryFromFile(creativeFile, playerInventoryCache,
                            inventorySpec, true);
                    inventories.saveInventory(null, playerInvEntry, false, false, false);
                }

                // Death
                for (int deathVersion = PlayerInventoryCache.DEATH_SAVE_MAX_NBR; deathVersion > 0; deathVersion--) {
                    final File deathFile = new File(invDir, String.format("%s.%s.%s.%s%s", playerUUID,
                            getGameModeFromBoolean(true), DEATH, deathVersion, INV_EXT));
                    if (deathFile.exists()) {
                        final PlayerInvEntry playerInvEntry = loadInventoryFromFile(deathFile, playerInventoryCache,
                                inventorySpec, false);
                        inventories.saveInventory(null, playerInvEntry, true, false, false);
                    }
                }
            }
        }
    }

    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, false, false);

    }

    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, true, false);
    }

    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        saveInventory(playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), true, false, false);
    }

    private PlayerInvEntry loadInventoryFromFile(final File playerItemFile,
            final PlayerInventoryCache playerInventoryCacheNullable, final InventorySpec inventorySpec,
            final boolean isCreative) {
        final YamlConfiguration configPlayerItemFile = new YamlConfiguration();
        final PlayerInvEntry playerInvEntry = new PlayerInvEntry(playerInventoryCacheNullable, inventorySpec,
                isCreative);

        try {
            // load Inventory
            configPlayerItemFile.load(playerItemFile);

            @SuppressWarnings("unused")
            final int version = configPlayerItemFile.getInt("Version");

            playerInvEntry.setLevel(configPlayerItemFile.getInt("Level"));
            playerInvEntry.setExp((float) configPlayerItemFile.getDouble("Exp"));

            playerInvEntry.setHealth(configPlayerItemFile.getDouble("Health"));
            playerInvEntry.setFoodLevel(configPlayerItemFile.getInt("FoodLevel"));

            final ItemStack[] itemListLoad = new ItemStack[PlayerInvEntry.INVENTORY_LIST_SIZE];
            final ItemStack[] itemEnderChest = new ItemStack[PlayerInvEntry.ENDER_CHEST_SIZE];
            for (int t = 0; t < itemListLoad.length; t++) {
                itemListLoad[t] = configPlayerItemFile.getItemStack("Slot." + t, null);
            }
            playerInvEntry.setSlotItems(itemListLoad);
            for (int t = 0; t < itemEnderChest.length; t++) {
                itemEnderChest[t] = configPlayerItemFile.getItemStack("EnderChest." + t, null);
            }
            playerInvEntry.setEnderChestItems(itemEnderChest);

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

    public void saveInventory(final PlayerInvEntry playerInvEntry, final UUID playerUUIDNullable,
            final boolean isDeathHistory, final boolean enderChestOnly, final boolean isDefaultInv) {
        final InventorySpec inventorySpec = playerInvEntry.getInventorySpec();

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (playerUUIDNullable == null && !inventorySpec.isSaveInventory()) {
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

        if (isDeathHistory && playerUUIDNullable != null) {
            // Save death inventory
            final String playerUUIDStr = playerUUIDNullable.toString();
            final String fileDeathPrefix = playerUUIDStr + "." + gmName + "." + DEATH + ".";
            filePreName = fileDeathPrefix + "1";

            // Death rename
            File actFile = new File(invDirFile, fileDeathPrefix + "9" + INV_EXT);
            if (actFile.exists()) {
                if (!actFile.delete()) {
                    secuboid.getLogger().severe("Unable to delete the file: " + actFile.getPath());
                }
            }
            for (int t = PlayerInventoryCache.DEATH_SAVE_MAX_NBR - 1; t >= 1; t--) {
                actFile = new File(invDirFile, fileDeathPrefix + t + INV_EXT);
                if (actFile.exists()) {
                    if (!actFile.renameTo(new File(invDirFile, fileDeathPrefix + (t + 1) + INV_EXT))) {
                        secuboid.getLogger().severe("Unable to rename the file: " + actFile.getPath());
                    }
                }
            }

        } else if (isDefaultInv) {
            // Save default inventory
            filePreName = DEFAULT_INV;

        } else {
            // Save normal inventory
            filePreName = playerUUIDNullable + "." + gmName;
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
                final ItemStack[] itemEnderChest = playerInvEntry.getEnderChestItems();
                for (int t = 0; t < itemListSave.length; t++) {
                    configPlayerItemFile.set("Slot." + t, itemListSave[t]);
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    configPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
                }

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

    public void purgeInventory(InventorySpec inventorySpec) {
        if (!secuboid.getInventoriesOpt().isPresent()) {
            return;
        }

        String inventoryName = inventorySpec.getInventoryName();

        File toDeleteDir = getInventoryDir(inventoryName);
        if (toDeleteDir.isDirectory()) {
            if (!FileUtil.delete(toDeleteDir)) {
                secuboid.getLogger().severe("Unable to delete the file: " + toDeleteDir.getPath());
            }
        }
    }

    private File getInventoryDir() {
        return new File(secuboid.getDataFolder(), INV_DIR);
    }

    private File getInventoryDir(final String invName) {
        return new File(getInventoryDir(), invName);
    }

    private String getGameModeFromBoolean(final boolean isCreative) {
        return isCreative ? "CREATIVE" : "SURVIVAL";
    }
}