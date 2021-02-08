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
package me.tabinol.secuboid.config;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;

/**
 * The class for inventory configuration.
 */
public final class InventoryConfig {

    public static final String GLOBAL = "default"; // Means it is assigned to all
    public static final String PERM_FORCESAVE = "secuboid.inv.forcesave";
    public static final String PERM_DEFAULT = "secuboid.inv.default";
    public static final String PERM_LOADDEATH = "secuboid.inv.loaddeath";
    public static final String PERM_LIST = "secuboid.inv.list";
    public static final String PERM_PURGE = "secuboid.inv.purge";
    public static final String PERM_IGNORE_CREATIVE_INV = "secuboid.inv.ignorecreativeinv";
    public static final String PERM_IGNORE_INV = "secuboid.inv.ignoreinv";
    public static final String PERM_IGNORE_DISABLED_COMMANDS = "secuboid.inv.ignoredisabledcommands";
    private static final String INVENTORY_CONFIG_FILE = "inventory.yml";

    private final Secuboid secuboid;

    private FlagType invFlag; // Registered inventory Flag (Factoid)
    private FileConfiguration config;
    private HashMap<String, InventorySpec> invNameToInvSpec;

    /**
     * Instantiates a new inventory configuration.
     *
     * @param secuboid secuboid instance
     */
    public InventoryConfig(final Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    /**
     * Load config.
     *
     * @param isServerBoot is first boot?
     */
    public void loadConfig(final boolean isServerBoot) {
        if (isServerBoot) {

            // Create files (if not exist) and load
            if (!new File(secuboid.getDataFolder(), INVENTORY_CONFIG_FILE).exists()) {
                secuboid.saveResource(INVENTORY_CONFIG_FILE, false);
            }

            // Connect to the data file and register flag to Secuboid
            invFlag = secuboid.getPermissionsFlags().registerFlagType("INVENTORY", "");
        }

        config = YamlConfiguration.loadConfiguration(new File(secuboid.getDataFolder(), INVENTORY_CONFIG_FILE));
        invNameToInvSpec = new HashMap<>();
        loadInventory();
    }

    private void loadInventory() {

        // Load World and Land inventories
        ConfigurationSection configSec = config.getConfigurationSection("Inventories");
        for (final Map.Entry<String, Object> invEntry : configSec.getValues(false).entrySet()) {
            if (invEntry.getValue() instanceof ConfigurationSection) {
                final boolean isCreativeChange = ((ConfigurationSection) invEntry.getValue())
                        .getBoolean("SeparateCreative", true);
                final boolean isSaveInventory = ((ConfigurationSection) invEntry.getValue()).getBoolean("SaveInventory",
                        true);
                final boolean isAllowDrop = ((ConfigurationSection) invEntry.getValue()).getBoolean("AllowDrop", true);
                final List<String> disabledCommands = ((ConfigurationSection) invEntry.getValue())
                        .getStringList("DisabledCommands");
                createInventoryEntry(invEntry.getKey().toLowerCase(), isCreativeChange, isSaveInventory, isAllowDrop,
                        disabledCommands);
            }
        }
    }

    private void createInventoryEntry(final String key, final boolean creativeChange, final boolean saveInventory,
            final boolean allowDrop, final List<String> disabledCommands) {
        invNameToInvSpec.put(key.toLowerCase(),
                new InventorySpec(key, creativeChange, saveInventory, allowDrop, disabledCommands));
    }

    public InventorySpec getInvSpec(LandPermissionsFlags dummyPermsFlags) {
        FlagValue invFlagValue = dummyPermsFlags.getFlagAndInherit(invFlag);

        // If the flag is not set
        if (invFlagValue.getValueString().isEmpty()) {
            return invNameToInvSpec.get(GLOBAL);
        }

        String invNameLower = invFlagValue.getValueString().toLowerCase();
        InventorySpec invSpec = invNameToInvSpec.get(invNameLower);

        // If the flag is set with wrong inventory
        if (invSpec == null) {
            secuboid.getLogger().info("Inventory name \"" + invFlagValue.getValueString() + "\" is not found " + "in "
                    + secuboid.getName() + "/inventory.yml! Creating a new one with the default configuration.");

            invSpec = createNoneExistingInvSpec(invNameLower);
        }

        return invSpec;
    }

    public Collection<InventorySpec> getInvSpecs() {
        return invNameToInvSpec.values();
    }

    public InventorySpec getInvSpec(String invName) {
        return invNameToInvSpec.get(invName.toLowerCase());
    }

    public InventorySpec getOrCreateInvSpec(String invName) {
        return invNameToInvSpec.computeIfAbsent(invName.toLowerCase(), this::createNoneExistingInvSpec);
    }

    private InventorySpec createNoneExistingInvSpec(String invNameLower) {
        InventorySpec invSpec = new InventorySpec(invNameLower, true, true, true, Collections.emptyList());
        invNameToInvSpec.put(invNameLower, invSpec);

        return invSpec;
    }
}
