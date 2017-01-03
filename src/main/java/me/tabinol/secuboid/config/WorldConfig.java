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
import java.util.Set;
import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;

import static me.tabinol.secuboid.config.Config.GLOBAL;

import me.tabinol.secuboid.lands.DefaultLand;
import me.tabinol.secuboid.lands.WorldLand;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The Class WorldConfig. Started by Lands.Class. Loads world config and lands default.
 */
public class WorldConfig {

    private final Secuboid secuboid;

    /**
     * The land default.
     */
    private final FileConfiguration landDefault;

    /**
     * The world config.
     */
    private final FileConfiguration worldConfig;

    /**
     * Default config (No Type or global)
     */
    private final DefaultLand defaultConfNoType;

    /**
     * Instantiates a new world config.
     *
     * @param secuboid secuboid instance
     */
    public WorldConfig(Secuboid secuboid) {

        this.secuboid = secuboid;
        File configFileFolder = secuboid.getDataFolder();

        // Create files (if not exist) and load
        if (!new File(configFileFolder, "landdefault.yml").exists()) {
            secuboid.saveResource("landdefault.yml", false);
        }
        if (!new File(configFileFolder, "worldconfig.yml").exists()) {
            secuboid.saveResource("worldconfig.yml", false);
        }
        landDefault = YamlConfiguration.loadConfiguration(new File(configFileFolder, "landdefault.yml"));
        worldConfig = YamlConfiguration.loadConfiguration(new File(configFileFolder, "worldconfig.yml"));

        // Create default (whitout type)
        defaultConfNoType = getLandDefaultConf();
    }

    /**
     * Gets the land outside area.
     *
     * @return the land outside area
     */
    public TreeMap<String, WorldLand> getLandOutsideArea() {

        TreeMap<String, WorldLand> landList = new TreeMap<String, WorldLand>();
        Set<String> keys = worldConfig.getConfigurationSection("").getKeys(false);

        // We have to take _global_ first then others
        for (String worldName : keys) {
            if (worldName.equalsIgnoreCase(GLOBAL)) {
                createConfForWorld(worldName, landList, false);
            }
        }

        // The none-global
        for (String worldName : keys) {
            if (!worldName.equalsIgnoreCase(GLOBAL)) {
                createConfForWorld(worldName, landList, true);
            }
        }

        return landList;
    }

    private void createConfForWorld(String worldName, TreeMap<String, WorldLand> landList, boolean copyFromGlobal) {

        String worldNameLower = worldName.toLowerCase();
        WorldLand dl = new WorldLand(secuboid, worldName);
        if (copyFromGlobal) {
            landList.get(GLOBAL).getPermissionsFlags().copyPermsFlagsTo(dl.getPermissionsFlags());
        }
        landModify(dl, worldConfig, worldName + ".ContainerPermissions", worldName + ".ContainerFlags");
        landList.put(worldNameLower, dl);
    }

    /**
     * Gets the land default conf.
     *
     * @return the land default conf
     */
    private DefaultLand getLandDefaultConf() {
        DefaultLand dl = new DefaultLand(secuboid, null);
        landModify(dl, landDefault, "ContainerPermissions", "ContainerFlags");
        return dl;
    }

    /**
     * Get the default configuration of a land without a Type.
     *
     * @return The land configuration (DummyLand)
     */
    public DefaultLand getDefaultconfNoType() {
        return defaultConfNoType;
    }

    /**
     * Gets the default conf for each type
     *
     * @return a TreeMap of default configuration
     */
    public TreeMap<Type, DefaultLand> getTypeDefaultConf() {
        TreeMap<Type, DefaultLand> defaultConf = new TreeMap<Type, DefaultLand>();

        for (Type type : secuboid.getTypes().getTypes()) {
            ConfigurationSection typeConf = landDefault.getConfigurationSection(type.getName());
            if (typeConf != null) {
                DefaultLand dl = new DefaultLand(secuboid, type);
                defaultConfNoType.getPermissionsFlags().copyPermsFlagsTo(dl.getPermissionsFlags());
                landModify(dl, typeConf, "ContainerPermissions", "ContainerFlags");
                defaultConf.put(type, dl);
            }
        }

        return defaultConf;
    }

    private void landModify(Land dl, ConfigurationSection fc, String perms, String flags) {

        ConfigurationSection csPerm = fc.getConfigurationSection(perms);
        ConfigurationSection csFlags = fc.getConfigurationSection(flags);

        // Add permissions
        if (csPerm != null) {
            for (String container : csPerm.getKeys(false)) {

                PlayerContainerType pcType = PlayerContainerType.getFromString(container);

                assert pcType != null;
                if (pcType.hasParameter()) {
                    for (String containerName : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        for (String perm : fc.getConfigurationSection(perms + "." + container + "." + containerName).getKeys(false)) {
                            // Remove _ if it is a Bukkit Permission
                            String containerNameLower;
                            if (pcType == PlayerContainerType.PERMISSION) {
                                containerNameLower = containerName.toLowerCase().replaceAll("_", ".");
                            } else {
                                containerNameLower = containerName.toLowerCase();
                            }

                            assert secuboid != null;
                            dl.getPermissionsFlags().addPermission(
                                    secuboid.getNewInstance().createPlayerContainer(null, pcType, containerNameLower),
                                    secuboid.getPermissionsFlags().newPermission(
                                            secuboid.getPermissionsFlags().getPermissionTypeNoValid(perm.toUpperCase()),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Inheritable")));
                        }
                    }
                } else {
                    for (String perm : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        dl.getPermissionsFlags().addPermission(
                                secuboid.getNewInstance().createPlayerContainer(null, pcType, null),
                                secuboid.getPermissionsFlags().newPermission(
                                        secuboid.getPermissionsFlags().getPermissionTypeNoValid(perm.toUpperCase()),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Value"),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Inheritable")));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (String flag : csFlags.getKeys(false)) {
                FlagType ft = secuboid.getPermissionsFlags().getFlagTypeNoValid(flag.toUpperCase());
                dl.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags().newFlag(ft,
                        secuboid.getNewInstance().getFlagValueFromFileFormat(fc.getString(flags + "." + flag + ".Value"), ft),
                        fc.getBoolean(flags + "." + flag + ".Inheritable")));
            }
        }
    }
}
