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
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.players.PlayerConfEntry;

/**
 * InventoriesFlat
 */
public class InventoriesFlat {

    private final static String INV_DIR = "inventories";
    private final static String DEFAULT_INV = "DEFAULTINV";
    private final static String DEATH = "DEATH";

    private final Secuboid secuboid;

    public InventoriesFlat(final Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    public void loadInventories() {
        // TODO Auto-generated method stub

    }

    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    public void loadInventoryPlayer(final PlayerConfEntry playerConfEntry) {
        // TODO Auto-generated method stub

    }

    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    private Optional<PlayerInvEntry> loadInventory(final PlayerConfEntry playerConfEntry,
            final InventorySpec inventorySpec, final boolean isCreative, final boolean fromDeath,
            final int deathVersion) {

        // Get the suffix name
        final String gmName = getGameModeFromBoolean(isCreative);
        final String suffixName;
        if (fromDeath) {
            suffixName = gmName + "." + DEATH + "." + deathVersion;
        } else {
            suffixName = gmName;
        }

        final YamlConfiguration configPlayerItemFile = new YamlConfiguration();

        // player item file
        File playerItemFile = new File(String.format("%s/%s/%s/%s.%s.yml", secuboid.getDataFolder(), INV_DIR,
                inventorySpec.getInventoryName(), playerConfEntry.getUUID(), suffixName));

        if (!fromDeath && !playerItemFile.exists()) {

            // Check for default inventory file
            playerItemFile = new File(String.format("%s/%s/%s/%s.yml", secuboid.getDataFolder(), INV_DIR,
                    inventorySpec.getInventoryName(), DEFAULT_INV));
        }

        // If no inventory exists
        if (!playerItemFile.exists()) {
            return Optional.empty();
        }

        try {
            // load Inventory
            final PlayerInvEntry playerInvEntry = new PlayerInvEntry(Optional.of(playerConfEntry), inventorySpec,
                    isCreative);
            configPlayerItemFile.load(playerItemFile);

            @SuppressWarnings("unused")
            final int version = configPlayerItemFile.getInt("Version");

            playerInvEntry.setLevel(configPlayerItemFile.getInt("Level"));
            playerInvEntry.setExp((float) configPlayerItemFile.getDouble("Exp"));

            playerInvEntry.setHealth(configPlayerItemFile.getDouble("Health"));
            playerInvEntry.setFoodLevel(configPlayerItemFile.getInt("FoodLevel"));

            final ItemStack[] itemListLoad = playerInvEntry.getItemListLoad();
            final ItemStack[] itemArmorLoad = playerInvEntry.getItemArmorLoad();
            final ItemStack[] itemEnderChest = playerInvEntry.getItemEnderChest();
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
            return Optional.of(playerInvEntry);

        } catch (final IOException ex) {
            secuboid.getLogger().log(Level.SEVERE, String.format("Error on inventory load for player %s, filename: %s",
                    playerConfEntry.getName(), playerItemFile.getPath()), ex);
            return Optional.empty();
        } catch (final InvalidConfigurationException ex) {
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Invalid configuration on inventory load for player %s, filename: %s",
                            playerConfEntry.getName(), playerItemFile.getPath()),
                    ex);
            return Optional.empty();
        }
    }

    private String getGameModeFromBoolean(final boolean isCreative) {
        return isCreative ? "CREATIVE" : "SURVIVAL";
    }
}