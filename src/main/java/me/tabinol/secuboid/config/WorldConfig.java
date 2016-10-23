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
import me.tabinol.secuboid.lands.DummyLand;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.parameters.FlagType;
import me.tabinol.secuboid.parameters.FlagValue;
import me.tabinol.secuboid.parameters.LandFlag;
import me.tabinol.secuboid.parameters.Permission;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.playercontainer.PlayerContainerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Started by Lands.Class
// Load world config and lands default
/**
 * The Class WorldConfig.
 */
public class WorldConfig {

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
    private final DummyLand defaultConfNoType;

    /**
     * Instantiates a new world config.
     */
    public WorldConfig() {

	File configFileFolder = Secuboid.getConfigFolder();

	// Create files (if not exist) and load
	if (!new File(configFileFolder, "landdefault.yml").exists()) {
	    Secuboid.getThisPlugin().saveResource("landdefault.yml", false);
	}
	if (!new File(configFileFolder, "worldconfig.yml").exists()) {
	    Secuboid.getThisPlugin().saveResource("worldconfig.yml", false);
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
    public TreeMap<String, DummyLand> getLandOutsideArea() {

	TreeMap<String, DummyLand> landList = new TreeMap<String, DummyLand>();
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

    private void createConfForWorld(String worldName, TreeMap<String, DummyLand> landList, boolean copyFromGlobal) {

	String worldNameLower = worldName.toLowerCase();
	if (Secuboid.getThisPlugin() != null) {
	    Secuboid.getThisPlugin().getLog().write("Create conf for World: " + worldNameLower);
	}
	DummyLand dl = new DummyLand(worldName);
	if (copyFromGlobal) {
	    landList.get(GLOBAL).copyPermsFlagsTo(dl);
	}
	landList.put(worldNameLower, landModify(dl, worldConfig,
		worldName + ".ContainerPermissions", worldName + ".ContainerFlags"));
    }

    /**
     * Gets the land default conf.
     *
     * @return the land default conf
     */
    private DummyLand getLandDefaultConf() {

	if (Secuboid.getThisPlugin() != null) {
	    Secuboid.getThisPlugin().getLog().write("Create default conf for lands");
	}
	return landModify(new DummyLand(GLOBAL), landDefault, "ContainerPermissions", "ContainerFlags");
    }

    /**
     * Get the default configuration of a land without a Type.
     *
     * @return The land configuration (DummyLand)
     */
    public DummyLand getDefaultconfNoType() {

	return defaultConfNoType;
    }

    /**
     * Gets the default conf for each type
     *
     * @return a TreeMap of default configuration
     */
    public TreeMap<Type, DummyLand> getTypeDefaultConf() {

	if (Secuboid.getThisPlugin() != null) {
	    Secuboid.getThisPlugin().getLog().write("Create default conf for lands");
	}
	TreeMap<Type, DummyLand> defaultConf = new TreeMap<Type, DummyLand>();

	for (Type type : Secuboid.getStaticTypes().getTypes()) {
	    ConfigurationSection typeConf = landDefault.getConfigurationSection(type.getName());
	    DummyLand dl = new DummyLand(type.getName());
	    defaultConfNoType.copyPermsFlagsTo(dl);
	    defaultConf.put(type, landModify(dl, typeConf,
		    "ContainerPermissions", "ContainerFlags"));
	}

	return defaultConf;
    }

    private DummyLand landModify(DummyLand dl, ConfigurationSection fc, String perms, String flags) {

	ConfigurationSection csPerm = null;
	ConfigurationSection csFlags = null;

	if (fc != null) {
	    csPerm = fc.getConfigurationSection(perms);
	    csFlags = fc.getConfigurationSection(flags);
	}

	// Add permissions
	if (csPerm != null) {
	    for (String container : csPerm.getKeys(false)) {

		PlayerContainerType pcType = PlayerContainerType.getFromString(container);

		if (pcType.hasParameter()) {
		    for (String containerName : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
			for (String perm : fc.getConfigurationSection(perms + "." + container + "." + containerName).getKeys(false)) {
			    if (Secuboid.getThisPlugin() != null) {
				Secuboid.getThisPlugin().getLog().write("Container: " + container + ":" + containerName + ", " + perm);
			    }

			    // Remove _ if it is a Bukkit Permission
			    String containerNameLower;
			    if (pcType == PlayerContainerType.PERMISSION) {
				containerNameLower = containerName.toLowerCase().replaceAll("_", ".");
			    } else {
				containerNameLower = containerName.toLowerCase();
			    }

			    dl.addPermission(
				    PlayerContainerUtil.create(null, pcType, containerNameLower),
				    new Permission(Secuboid.getStaticParameters().getPermissionTypeNoValid(perm.toUpperCase()),
					    fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
					    fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Heritable")));
			}
		    }
		} else {
		    for (String perm : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
			if (Secuboid.getThisPlugin() != null) {
			    Secuboid.getThisPlugin().getLog().write("Container: " + container + ", " + perm);
			}
			dl.addPermission(
				PlayerContainerUtil.create(null, pcType, null),
				new Permission(Secuboid.getStaticParameters().getPermissionTypeNoValid(perm.toUpperCase()),
					fc.getBoolean(perms + "." + container + "." + perm + ".Value"),
					fc.getBoolean(perms + "." + container + "." + perm + ".Heritable")));
		    }
		}
	    }
	}

	// add flags
	if (csFlags != null) {
	    for (String flag : csFlags.getKeys(false)) {
		if (Secuboid.getThisPlugin() != null) {
		    Secuboid.getThisPlugin().getLog().write("Flag: " + flag);
		}
		FlagType ft = Secuboid.getStaticParameters().getFlagTypeNoValid(flag.toUpperCase());
		dl.addFlag(new LandFlag(ft,
			FlagValue.getFromString(fc.getString(flags + "." + flag + ".Value"), ft),
			fc.getBoolean(flags + "." + flag + ".Heritable")));
	    }
	}

	return dl;
    }
}
