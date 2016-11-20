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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.lands.Land;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The inventory storage class.
 */
public class InventoryStorage {

    public final static String INV_DIR = "inventories";
    public final static String DEFAULT_INV = "DEFAULTINV";
    public final static int MAX_FOOD_LEVEL = 20;
    public final static String DEATH = "DEATH";

    private final Secuboid secuboid;
    private final int storageVersion;
    private final HashMap<Player, PlayerInvEntry> playerInvList; // Last inventory

    /**
     * Player Join, Quit, Change
     */
    public enum PlayerAction {
        JOIN,
        QUIT,
        CHANGE,
        DEATH
    }

    /**
     * Get a name of Game Mode Inventory type
     */
    public enum InventoryType {
        CREATIVE,
        SURVIVAL;

        public static InventoryType getFromBoolean(boolean isCreative) {
            if (isCreative) {
                return CREATIVE;
            } else {
                return SURVIVAL;
            }
        }
    }

    /**
     * Instantiates a new inventory storage.
     *
     * @param secuboid secuboid instance
     */
    public InventoryStorage(Secuboid secuboid) {

        this.secuboid = secuboid;
        storageVersion = secuboid.getMavenAppProperties().getPropertyInt("inventoryStorageVersion");
        playerInvList = new HashMap<Player, PlayerInvEntry>();
    }

    public void saveInventory(Player player, String invName, boolean isCreative,
                              boolean isDeath, boolean isSaveAllowed, boolean isDefaultInv, boolean enderChestOnly) throws IOException {

        // If for some reasons whe have to skip save (ex: SaveInventory = false)
        if (!isSaveAllowed) {
            return;
        }

        File file;
        String filePreName;

        // Create directories (if not here)
        file = new File(secuboid.getDataFolder() + "/" + INV_DIR);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new IOException("Impossible to create the directory " + file.getPath() + ".");
            }
        }
        file = new File(secuboid.getDataFolder() + "/" + INV_DIR + "/" + invName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new IOException("Impossible to create the directory " + file.getPath() + ".");
            }
        }

        // Get the suffix name
        String gmName = InventoryType.getFromBoolean(isCreative).name();

        if (isDeath) {

            filePreName = player.getUniqueId().toString() + "." + gmName + "." + DEATH + ".1";

            // Death rename
            File actFile = new File(file, "/" + player.getUniqueId().toString() + "." + gmName + "." + DEATH + ".9.yml");
            if (actFile.exists()) {
                if (!actFile.delete()) {
                    throw new IOException("Impossible to delete the file " + actFile.getPath() + ".");
                }
            }
            for (int t = 8; t >= 1; t--) {
                actFile = new File(file, "/"
                        + player.getUniqueId().toString() + "." + gmName + "." + DEATH + "." + t + ".yml");
                if (actFile.exists()) {
                    if (!actFile.renameTo(new File(file, "/"
                            + player.getUniqueId().toString() + "." + gmName + "." + DEATH + "." + (t + 1) + ".yml"))) {
                        throw new IOException("Impossible to rename the file " + actFile.getPath() + ".");
                    }
                }
            }

        } else if (isDefaultInv) {

            // Save default inventory
            filePreName = DEFAULT_INV;

        } else {

            // Save normal inventory
            filePreName = player.getUniqueId().toString() + "." + gmName;
        }

        // Save Inventory
        YamlConfiguration ConfigPlayerItemFile = new YamlConfiguration();
        File playerItemFile = new File(file, "/" + filePreName + ".yml");

        try {

            ConfigPlayerItemFile.set("Version", storageVersion);

            // Save Only ender chest (Death)
            if (enderChestOnly) {
                ConfigPlayerItemFile.set("Level", 0);
                ConfigPlayerItemFile.set("Exp", 0f);
                ConfigPlayerItemFile.set("Health", player.getMaxHealth());
                ConfigPlayerItemFile.set("FoodLevel", MAX_FOOD_LEVEL);

                ItemStack[] itemEnderChest = player.getEnderChest().getContents();
                for (int t = 0; t < 4; t++) {
                    ConfigPlayerItemFile.set("Armor." + t, new ItemStack(Material.AIR));
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    ConfigPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
                }
            } else {

                // Save all
                ConfigPlayerItemFile.set("Level", player.getLevel());
                ConfigPlayerItemFile.set("Exp", player.getExp());
                ConfigPlayerItemFile.set("Health", player.getHealth());
                ConfigPlayerItemFile.set("FoodLevel", player.getFoodLevel());

                ItemStack[] itemListSave = player.getInventory().getContents();
                ItemStack[] itemArmorSave = player.getInventory().getArmorContents();
                ItemStack[] itemEnderChest = player.getEnderChest().getContents();
                for (int t = 0; t < itemListSave.length; t++) {
                    ConfigPlayerItemFile.set("Slot." + t, itemListSave[t]);
                }
                for (int t = 0; t < itemArmorSave.length; t++) {
                    ConfigPlayerItemFile.set("Armor." + t, itemArmorSave[t]);
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    ConfigPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
                }

                // PotionsEffects
                Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
                ConfigurationSection effectSection = ConfigPlayerItemFile.createSection("PotionEffect");
                for (PotionEffect effect : activePotionEffects) {
                    ConfigurationSection effectSubSection = effectSection.createSection(effect.getType().getName());
                    effectSubSection.set("Duration", effect.getDuration());
                    effectSubSection.set("Amplifier", effect.getAmplifier());
                    effectSubSection.set("Ambient", effect.isAmbient());
                }
            }

            ConfigPlayerItemFile.save(playerItemFile);

        } catch (IOException ex) {
            Logger.getLogger(InventoryStorage.class.getName()).log(Level.SEVERE,
                    "Error on inventory save for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
        }
    }

    /**
     * Loads the inventory for a player.
     *
     * @param player       the player
     * @param invName      the inventory name
     * @param isCreative   is creative inventory
     * @param fromDeath    is death before
     * @param deathVersion the death version
     * @return true is the inventory exist
     */
    public boolean loadInventory(Player player, String invName,
                                 boolean isCreative, boolean fromDeath, int deathVersion) {

        boolean invExist = false;
        String suffixName;

        // Get the suffix name
        String gmName = InventoryType.getFromBoolean(isCreative).name();
        if (fromDeath) {
            suffixName = gmName + "." + DEATH + "." + deathVersion;
        } else {
            suffixName = gmName;
        }

        YamlConfiguration ConfigPlayerItemFile = new YamlConfiguration();

        // player item file
        File playerItemFile = new File(secuboid.getDataFolder() + "/" + INV_DIR + "/"
                + invName + "/" + player.getUniqueId().toString() + "." + suffixName + ".yml");

        if (!fromDeath && !playerItemFile.exists()) {

            // Check for default inventory file
            playerItemFile = new File(secuboid.getDataFolder() + "/" + INV_DIR + "/"
                    + invName + "/" + DEFAULT_INV + ".yml");
        }

        if (playerItemFile.exists()) {

            invExist = true;

            try {

                // load Inventory
                ConfigPlayerItemFile.load(playerItemFile);

                @SuppressWarnings("unused")
                int version = ConfigPlayerItemFile.getInt("Version");

                player.setTotalExperience(ConfigPlayerItemFile.getInt("Experience"));
                player.setLevel(ConfigPlayerItemFile.getInt("Level"));
                player.setExp((float) ConfigPlayerItemFile.getDouble("Exp"));

                if (!fromDeath) {
                    double healt = ConfigPlayerItemFile.getDouble("Health");
                    if (healt > 0) {
                        player.setHealth(healt);
                        player.setFoodLevel(ConfigPlayerItemFile.getInt("FoodLevel"));
                    } else {
                        // Fix Death infinite loop
                        player.setHealth(player.getMaxHealth());
                        player.setFoodLevel(MAX_FOOD_LEVEL);
                    }
                }

                ItemStack[] itemListLoad = new ItemStack[36];
                ItemStack[] itemArmorLoad = new ItemStack[4];
                ItemStack[] itemEnderChest = new ItemStack[27];
                for (int t = 0; t < itemListLoad.length; t++) {
                    itemListLoad[t] = ConfigPlayerItemFile.getItemStack("Slot." + t);
                }
                for (int t = 0; t < itemArmorLoad.length; t++) {
                    itemArmorLoad[t] = ConfigPlayerItemFile.getItemStack("Armor." + t);
                }
                for (int t = 0; t < itemEnderChest.length; t++) {
                    itemEnderChest[t] = ConfigPlayerItemFile.getItemStack("EnderChest." + t);
                }

                player.getInventory().setContents(itemListLoad);
                player.getInventory().setArmorContents(itemArmorLoad);
                player.getEnderChest().setContents(itemEnderChest);

                // PotionsEffects
                removePotionEffects(player);
                ConfigurationSection effectSection = ConfigPlayerItemFile.getConfigurationSection("PotionEffect");
                if (effectSection != null) {
                    for (Map.Entry<String, Object> effectEntry : effectSection.getValues(false).entrySet()) {

                        PotionEffectType type = PotionEffectType.getByName(effectEntry.getKey());
                        ConfigurationSection effectSubSection = (ConfigurationSection) effectEntry.getValue();
                        int duration = effectSubSection.getInt("Duration");
                        int amplifier = effectSubSection.getInt("Amplifier");
                        boolean ambient = effectSubSection.getBoolean("Ambient");
                        player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient), true);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(InventoryStorage.class.getName()).log(Level.SEVERE,
                        "Error on inventory load for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(InventoryStorage.class.getName()).log(Level.SEVERE,
                        "Invalid configuration on inventory load for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
            }
        } else if (!fromDeath) {

            // The file is not existing, only clear all inventory
            player.setLevel(0);
            player.setExp(0);
            player.setHealth(player.getMaxHealth());
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

    private void removePotionEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public void switchInventory(Player player, Land land, boolean toIsCreative, PlayerAction playerAction) {

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
            fromInv = invEntry.getActualInv();
        }

        // Get new inventory
        toInv = secuboid.getInventoryConf().getInvSpec(land);

        // check if we have to do this action
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_INV)) {
            return;
        }

        // Force survival value if we do not change to creative inventory
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
                || (fromInv != null && !fromInv.isCreativeChange())) {
            fromIsCreative = false;
        }
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
                || !toInv.isCreativeChange()) {
            toIsCreative = false;
        }

        // Update player inventory information
        if (playerAction != PlayerAction.QUIT) {
            playerInvList.put(player, new PlayerInvEntry(toInv, toIsCreative));
        }

        // Return if the inventory will be exacly the same
        if (playerAction != PlayerAction.DEATH && playerAction != PlayerAction.QUIT
                && (fromInv != null && fromInv.getInventoryName().equals(toInv.getInventoryName())
                && fromIsCreative == toIsCreative)) {
            return;
        }

        try {
            // If the player is death, save a renamed file
            if (playerAction == PlayerAction.DEATH && fromInv != null) {
                saveInventory(player, fromInv.getInventoryName(), fromIsCreative,
                        true, fromInv.isSaveInventory(), false, false);
            }

            // Save last inventory (only EnderChest if death)
            if (playerAction != PlayerAction.JOIN && fromInv != null) {
                saveInventory(player, fromInv.getInventoryName(), fromIsCreative,
                        false, fromInv.isSaveInventory(), false, playerAction == PlayerAction.DEATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public PlayerInvEntry getPlayerInvEntry(Player player) {
        return playerInvList.get(player);
    }
}
