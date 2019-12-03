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

import static me.tabinol.secuboid.config.Config.GLOBAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.Yaml;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.DefaultLand;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.WorldLand;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;

/**
 * The Class WorldConfig. Started by Lands.Class. Loads world config and lands
 * default.
 */
public final class WorldConfig {

    private static final String KEY_PERMISSIONS = "permissions";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_PLAYER_CONTAINERS = "playercontainers";
    private static final String KEY_WORLDS = "worlds";
    private static final String KEY_TYPES = "types";
    private static final String KEY_VALUE = "value";
    private static final String KEY_INHERITABLE = "inheritable";

    private final Logger log;
    private final Secuboid secuboid;

    /**
     * Default config (No Type or global)
     */
    private DefaultLand defaultConfNoType;

    /**
     * Instantiates a new world config.
     *
     * @param secuboid secuboid instance
     */
    public WorldConfig(final Secuboid secuboid) {
        this.log = secuboid.getLogger();
        this.secuboid = secuboid;
    }

    public void loadResources() {
        Arrays.stream(FileType.values()).forEach(this::loadData);
    }

    @SuppressWarnings("unchecked")
    private void loadData(FileType fileType) {
        final String fileName = fileType.fileName;

        // Create files (if not exist) and load
        final File configFileFolder = secuboid.getDataFolder();
        if (!new File(configFileFolder, fileName).exists()) {
            secuboid.saveResource(fileName, false);
        }

        // Load yaml
        final Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(configFileFolder, fileName));
        } catch (final FileNotFoundException e) {
            log.log(Level.SEVERE, String.format("Unable to load %s!", fileName), e);
            return;
        }
        final Map<String, Object> root = (Map<String, Object>) yaml.load(inputStream);
        for (final Map.Entry<String, Object> entry : root.entrySet()) {
            final String keyName = entry.getKey();
            final Object valueObj = entry.getValue();
            final ParameterType parameterType;
            try {
                parameterType = ParameterType.valueOf(keyName.toLowerCase());
            } catch (IllegalArgumentException e) {
                log.severe(String.format("In file %s, invalid tag name: \"%s\"", fileName, keyName));
                continue;
            }

            // Check if the key is a list
            if (!(valueObj instanceof List)) {
                log.severe(String.format("In file %s, key \"%s\" must be a list: - ...", fileName, keyName));
                continue;
            }

            // Load permissions and flags
            loadFlagPerm(fileType, parameterType, keyName, (List<Object>) valueObj);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFlagPerm(FileType fileType, ParameterType parameterType, String rootKey, List<Object> objects) {
        for (Object keyToValueObj : objects) {
            if (!(keyToValueObj instanceof Map)) {
                log.severe(String.format("In file %s, invalid format for permissions", fileType.fileName));
                continue;
            }

            // Load flags/perms list
            final FlagPermValues flagPermValues = loadFlagPermValues(fileType, rootKey, (Map<String, Object>) keyToValueObj, parameterType);

            if (parameterType == ParameterType.FLAG) {
                if (flagPermValues.flagsNullable == null || flagPermValues.flagsNullable.isEmpty() || flagPermValues.valueNullable == null) {
                    log.severe(String.format("In file %s, a flag must have at least a flag name and a value", fileType.fileName));
                    continue;
                }
                // TODO Load flag
            }

            if (parameterType == ParameterType.PERMISSION) {
                if (flagPermValues.permissionsNullable == null || flagPermValues.permissionsNullable.isEmpty() || flagPermValues.playerContainersNullable == null || flagPermValues.playerContainersNullable.isEmpty() || flagPermValues.valueNullable == null) {
                    log.severe(String.format("In file %s, a permission must have at least a player container, a permission name and a value (true/false)", fileType.fileName));
                    continue;
                }
                // TODO Load permission
            }
        }

    }

    private FlagPermValues loadFlagPermValues(FileType fileType, String rootKey, final Map<String, Object> keyToValue,
            ParameterType parameterType) {
        final String fileName = fileType.fileName;
        final FlagPermValues flagPermValues = new FlagPermValues();

        for (final Map.Entry<String, Object> entry : keyToValue.entrySet()) {
            final String keyName = entry.getKey();
            final Object valueObj = entry.getValue();
            switch (keyName.toLowerCase()) {
            case KEY_PLAYER_CONTAINERS:
                if (parameterType == ParameterType.PERMISSION) {
                    flagPermValues.playerContainersNullable = loadStringList(valueObj);
                } else {
                    loadFlagPermErrorMsg(fileName, keyName, rootKey);
                }
                break;

            case KEY_PERMISSIONS:
                if (parameterType == ParameterType.PERMISSION) {
                    flagPermValues.permissionsNullable = loadStringList(valueObj);
                } else {
                    loadFlagPermErrorMsg(fileName, keyName, rootKey);
                }
                break;

            case KEY_FLAGS:
                if (parameterType == ParameterType.FLAG) {
                    flagPermValues.flagsNullable = loadStringList(valueObj);
                } else {
                    loadFlagPermErrorMsg(fileName, keyName, rootKey);
                }
                break;

            case KEY_WORLDS:
                if (fileType == FileType.WORLD_CONFIG) {
                    flagPermValues.worldsNullable = loadStringList(valueObj);
                } else {
                    loadFlagPermErrorMsg(fileName, keyName, rootKey);
                }
                break;

            case KEY_TYPES:
                if (fileType == FileType.LAND_DEFAULT) {
                    flagPermValues.typesNullable = loadStringList(valueObj);
                } else {
                    loadFlagPermErrorMsg(fileName, keyName, rootKey);
                }
                break;

            case KEY_VALUE:
                flagPermValues.valueNullable = getBooleanNullable(valueObj);
                if (flagPermValues.valueNullable == null) {
                    if (parameterType == ParameterType.PERMISSION) {
                        log.severe(String.format(
                                "In file %s, the permission value must be true or false: \"%s: %s\" for root key \"%s\"",
                                fileName, keyName, Objects.toString(valueObj), rootKey));
                    } else {
                        flagPermValues.valueNullable = valueObj;
                    }
                }
                break;

            case KEY_INHERITABLE:
                flagPermValues.inheritableNullable = getBooleanNullable(valueObj);
                if (flagPermValues.inheritableNullable == null) {
                    log.severe(String.format(
                            "In file %s, inheritable must be true or false: \"%s: %s\" for root key \"%s\"", fileName,
                            keyName, Objects.toString(valueObj), rootKey));
                }
                break;

            default:
                log.severe(String.format("In file %s, invalid key: \"%s: %s\" for root key \"%s\"", fileName, keyName,
                        Objects.toString(valueObj), rootKey));
            }
        }
        return flagPermValues;
    }

    private void loadFlagPermErrorMsg(String fileName, String keyName, String rootKey) {
        log.severe(
                String.format("In file %s, invalid tag name: \"%s\" for root key \"%s\"", fileName, keyName, rootKey));
    }

    @SuppressWarnings("unchecked")
    private List<String> loadStringList(Object stringListObj) {
        if (stringListObj instanceof List) {
            return (List<String>) stringListObj;
        }
        return Collections.singletonList(Objects.toString(stringListObj));
    }

    private Boolean getBooleanNullable(Object valueObj) {
        if (valueObj instanceof Boolean) {
            return (Boolean) valueObj;
        }
        final String valueStr = Objects.toString(valueObj);
        if (valueStr != null) {
            if (valueStr.matches("^(?i)(true|yes)$")) {
                return true;
            }
            if (valueStr.matches("^(?i)(false|no)$")) {
                return false;
            }
        }
        return null;
    }

    private enum ParameterType {
        PERMISSION("permissions"), FLAG("flags");

        final String parameterName;

        private ParameterType(String parameterName) {
            this.parameterName = parameterName;
        }
    }

    private enum FileType {
        LAND_DEFAULT("landdefault.yml"), WORLD_CONFIG("worldconfig.yml");

        final String fileName;

        private FileType(String fileName) {
            this.fileName = fileName;
        }
    }

    private static class FlagPermValues {
        List<String> playerContainersNullable = null;
        List<String> permissionsNullable = null;
        List<String> flagsNullable = null;
        List<String> worldsNullable = null;
        List<String> typesNullable = null;
        Object valueNullable = null;
        Boolean inheritableNullable = null;
    }

    public TreeMap<String, WorldLand> getLandOutsideArea() {
        // TODO
        return null;
    }

    public DefaultLand getDefaultconfNoType() {
        // TODO
        return null;
    }

    public TreeMap<Type, DefaultLand> getTypeDefaultConf() {
        // TODO
        return null;
    }
}
