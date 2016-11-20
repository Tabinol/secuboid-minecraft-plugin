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
package me.tabinol.secuboid.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The class for inventory configuration.
 */
public final class InventoryConfig {

    public static final String GLOBAL = "Default"; // Means it is assigned to all
    public static final String PERM_FORCESAVE = "secuboid.inv.forcesave";
    public static final String PERM_DEFAULT = "secuboid.inv.default";
    public static final String PERM_LOADDEATH = "secuboid.inv.loaddeath";
    public static final String PERM_IGNORE_CREATIVE_INV = "secuboid.inv.ignorecreativeinv";
    public static final String PERM_IGNORE_INV = "secuboid.inv.ignoreinv";
    public static final String PERM_IGNORE_DISABLED_COMMANDS = "secuboid.inv.ignoredisabledcommands";
    private static final String INVENTORY_CONFIG_FILE = "inventory.yml";

    private final FlagType invFlag; // Registered inventory Flag (Factoid)
    private final Secuboid secuboid;
    private FileConfiguration config;
    private HashMap<String, InventorySpec> invList; // World-->Land-->Inventory

    /**
     * Instantiates a new inventory configuration.
     *
     * @param secuboid secuboid instance
     */
    public InventoryConfig(Secuboid secuboid) {

        this.secuboid = secuboid;

        // Create files (if not exist) and load
        if (!new File(secuboid.getDataFolder(), INVENTORY_CONFIG_FILE).exists()) {
            secuboid.saveResource(INVENTORY_CONFIG_FILE, false);
        }

        // Connect to the data file and register flag to Factoid
        invFlag = secuboid.getPermissionsFlags().registerFlagType("INVENTORY", "");

        reloadConfig();
    }

    public void reloadConfig() {

        config = YamlConfiguration.loadConfiguration(new File(secuboid.getDataFolder(), INVENTORY_CONFIG_FILE));
        invList = new HashMap<String, InventorySpec>();
        loadInventory();
    }

    private void loadInventory() {

        // Load World and Land inventories
        ConfigurationSection configSec = config.getConfigurationSection("Inventories");
        for (Map.Entry<String, Object> invEntry : configSec.getValues(false).entrySet()) {
            if (invEntry.getValue() instanceof ConfigurationSection) {
                boolean isCreativeChange = ((ConfigurationSection) invEntry.getValue()).getBoolean("SeparateCreative", true);
                boolean isSaveInventory = ((ConfigurationSection) invEntry.getValue()).getBoolean("SaveInventory", true);
                boolean isAllowDrop = ((ConfigurationSection) invEntry.getValue()).getBoolean("AllowDrop", true);
                List<String> disabledCommands = ((ConfigurationSection) invEntry.getValue()).getStringList("DisabledCommands");
                createInventoryEntry(invEntry.getKey(), isCreativeChange, isSaveInventory, isAllowDrop, disabledCommands);
            }
        }
    }

    private void createInventoryEntry(String key, boolean creativeChange, boolean saveInventory, boolean allowDrop,
                                      List<String> disabledCommands) {

        invList.put(key, new InventorySpec(key, creativeChange, saveInventory, allowDrop, disabledCommands));
    }

    public InventorySpec getInvSpec(Land dummyLand) {

        FlagValue invFlagValue = dummyLand.getPermissionsFlags().getFlagAndInherit(invFlag);

        // If the flag is not set
        if (invFlagValue.getValueString().isEmpty()) {
            return invList.get(GLOBAL);
        }

        InventorySpec invSpec = invList.get(invFlagValue.getValueString());

        // If the flag is set with wrong inventory
        if (invSpec == null) {
            secuboid.getLog().warning("Inventory name \"" + invFlagValue.getValueString() + "\" is not found " + "in " + secuboid.getName() + "/plugin.yml!");
            return invList.get(GLOBAL);
        }

        return invSpec;
    }
}
