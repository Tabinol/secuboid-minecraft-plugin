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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;

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

    /**
     * The global permissions and flags.
     */
    private final LandPermissionsFlags globalPermissionsFlags;

    /**
     * Per world permissions and flags.
     */
    private final Map<String, LandPermissionsFlags> worldNameToPermissionsFlags;

    /**
     * Default configuration (Type not exist or Type null).
     */
    private final LandPermissionsFlags defaultPermissionsFlags;

    /**
     * The default configuration for a land.
     */
    private final Map<Type, LandPermissionsFlags> typeToDefaultPermissionsFlags;

    private final Logger log;
    private final Secuboid secuboid;

    /**
     * Instantiates a new world config.
     *
     * @param secuboid secuboid instance
     */
    public WorldConfig(final Secuboid secuboid) {
        this.log = secuboid.getLogger();
        this.secuboid = secuboid;
        globalPermissionsFlags = new LandPermissionsFlags(secuboid, null);
        worldNameToPermissionsFlags = new HashMap<>();
        defaultPermissionsFlags = new LandPermissionsFlags(secuboid);
        typeToDefaultPermissionsFlags = new HashMap<>();
    }

    public void loadResources() {
        // Load resources
        globalPermissionsFlags.setDefault();
        worldNameToPermissionsFlags.clear();
        defaultPermissionsFlags.setDefault();
        typeToDefaultPermissionsFlags.clear();
        Arrays.stream(FileType.values()).forEach(this::loadData);

        // Copy global permissions whitout override existing
        worldNameToPermissionsFlags.forEach((k, v) -> globalPermissionsFlags.copyPermsFlagsToWithoutOverride(v));
        typeToDefaultPermissionsFlags.forEach((k, v) -> defaultPermissionsFlags.copyPermsFlagsToWithoutOverride(v));
    }

    public LandPermissionsFlags getGlobalPermissionsFlags() {
        return globalPermissionsFlags;
    }

    public Map<String, LandPermissionsFlags> getWorldNameToPermissionsFlags() {
        return worldNameToPermissionsFlags;
    }

    public LandPermissionsFlags getDefaultPermissionsFlags() {
        return defaultPermissionsFlags;
    }

    public Map<Type, LandPermissionsFlags> getTypeToDefaultPermissionsFlags() {
        return typeToDefaultPermissionsFlags;
    }

    @SuppressWarnings("unchecked")
    private void loadData(final FileType fileType) {
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
            try {
                final String keyName = entry.getKey();
                final Object valueObj = entry.getValue();
                final ParameterType parameterType = getParameterTypeFromString(fileName, keyName);

                // Check if the key is a list
                if (!(valueObj instanceof List)) {
                    throw new WorldConfigException(
                            String.format("In file %s, key \"%s\" must be a list: - ...", fileName, keyName));
                }

                // Load permissions and flags
                loadFlagPerm(fileType, parameterType, keyName, (List<Object>) valueObj);
            } catch (final WorldConfigException ex) {
                log.severe(ex.getMessage());
            }
        }
    }

    final ParameterType getParameterTypeFromString(final String fileName, final String keyName)
            throws WorldConfigException {
        final ParameterType parameterType;
        try {
            parameterType = ParameterType.valueOf(keyName.toLowerCase());
        } catch (final IllegalArgumentException e) {
            throw new WorldConfigException(String.format("In file %s, invalid tag name: \"%s\"", fileName, keyName), e);
        }
        return parameterType;
    }

    @SuppressWarnings("unchecked")
    private void loadFlagPerm(final FileType fileType, final ParameterType parameterType, final String rootKey,
            final List<Object> objects) {
        for (final Object keyToValueObj : objects) {
            if (keyToValueObj instanceof Map) {
                loadFlagPermFromKey(fileType, parameterType, rootKey, (Map<String, Object>) keyToValueObj);
            } else {
                log.severe(String.format("In file %s, invalid format for permissions", fileType.fileName));
            }
        }
    }

    private void loadFlagPermFromKey(final FileType fileType, final ParameterType parameterType, final String rootKey,
            final Map<String, Object> keyToValue) {
        // Load flags/perms list
        final FlagPermValues flagPermValues = loadFlagPermValues(fileType, rootKey, keyToValue, parameterType);

        if (parameterType == ParameterType.FLAG) {
            if (!flagPermValues.verifyForFlags()) {
                log.severe(String.format("In file %s, a flag must have at least a flag name and a value",
                        fileType.fileName));
                return;
            }
            loadFlags(fileType, flagPermValues);
        }

        if (parameterType == ParameterType.PERMISSION) {
            if (!flagPermValues.verifyForPermissions()) {
                log.severe(String.format(
                        "In file %s, a permission must have at least a player container, a permission name and a value (true/false)",
                        fileType.fileName));
                return;
            }
            loadPermissions(fileType, flagPermValues);
        }
    }

    private FlagPermValues loadFlagPermValues(final FileType fileType, final String rootKey,
            final Map<String, Object> keyToValue, final ParameterType parameterType) {
        final FlagPermValues flagPermValues = new FlagPermValues();

        for (final Map.Entry<String, Object> entry : keyToValue.entrySet()) {
            final String keyName = entry.getKey();
            final Object valueObj = entry.getValue();
            loadFlagPermValue(flagPermValues, fileType, rootKey, parameterType, keyName, valueObj);
        }
        return flagPermValues;
    }

    private void loadFlagPermValue(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final ParameterType parameterType, final String keyName, final Object valueObj) {
        final String fileName = fileType.fileName;
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
            flagPermValues.valueNullable = getBoolean(valueObj);
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
            flagPermValues.inheritableOpt = getBoolean(valueObj);
            if (!flagPermValues.inheritableOpt.isPresent()) {
                log.severe(
                        String.format("In file %s, inheritable must be true or false: \"%s: %s\" for root key \"%s\"",
                                fileName, keyName, Objects.toString(valueObj), rootKey));
            }
            break;

        default:
            log.severe(String.format("In file %s, invalid key: \"%s: %s\" for root key \"%s\"", fileName, keyName,
                    Objects.toString(valueObj), rootKey));
        }
    }

    private void loadFlagPermErrorMsg(final String fileName, final String keyName, final String rootKey) {
        log.severe(
                String.format("In file %s, invalid tag name: \"%s\" for root key \"%s\"", fileName, keyName, rootKey));
    }

    @SuppressWarnings("unchecked")
    private List<String> loadStringList(final Object stringListObj) {
        if (stringListObj instanceof List) {
            return (List<String>) stringListObj;
        }
        return Collections.singletonList(Objects.toString(stringListObj));
    }

    private Optional<Boolean> getBoolean(final Object valueObj) {
        if (valueObj instanceof Boolean) {
            return Optional.of((Boolean) valueObj);
        }
        final String valueStr = Objects.toString(valueObj);
        if (valueStr != null) {
            if (valueStr.matches("^(?i)(true|yes)$")) {
                return Optional.of(Boolean.TRUE);
            }
            if (valueStr.matches("^(?i)(false|no)$")) {
                return Optional.of(Boolean.FALSE);
            }
        }
        return Optional.empty();
    }

    private final void loadPermissions(final FileType fileType, final FlagPermValues flagPermValues) {
        for (final String playerContainerName : flagPermValues.playerContainersNullable) {
            for (final String permissionName : flagPermValues.permissionsNullable) {
                try {
                    loadPermission(fileType, flagPermValues, playerContainerName, permissionName);
                } catch (final WorldConfigException e) {
                    log.severe(e.getMessage());

                }
            }
        }
    }

    private final void loadPermission(final FileType fileType, final FlagPermValues flagPermValues,
            final String playerContainerName, final String permissionName) throws WorldConfigException {
        final Map.Entry<PlayerContainer, Permission> playerContainerToPermission = createPcPermissionNullable(
                fileType.fileName, playerContainerName, permissionName, (boolean) flagPermValues.valueNullable,
                flagPermValues.inheritableOpt.orElse(Boolean.TRUE));
        final PlayerContainer playerContainer = playerContainerToPermission.getKey();
        final Permission permission = playerContainerToPermission.getValue();
        if (fileType == FileType.LAND_DEFAULT) {
            loadPermissionLandDefault(flagPermValues, playerContainer, permission);
        } else {
            loadPermissionWorldConfig(flagPermValues, playerContainer, permission);
        }
    }

    private void loadPermissionLandDefault(final FlagPermValues flagPermValues, final PlayerContainer playerContainer,
            final Permission permission) {
        if (flagPermValues.typesNullable == null || flagPermValues.typesNullable.isEmpty()) {
            defaultPermissionsFlags.addPermission(playerContainer, permission);
        } else {
            flagPermValues.typesNullable.forEach(typeName -> {
                final String typeNameLower = typeName.toLowerCase();
                final Type type = secuboid.getTypes().addOrGetType(typeNameLower);
                typeToDefaultPermissionsFlags.computeIfAbsent(type, k -> new LandPermissionsFlags(secuboid))
                        .addPermission(playerContainer, permission);
            });
        }
    }

    private void loadPermissionWorldConfig(final FlagPermValues flagPermValues, final PlayerContainer playerContainer,
            final Permission permission) {
        if (flagPermValues.worldsNullable == null || flagPermValues.worldsNullable.isEmpty()) {
            globalPermissionsFlags.addPermission(playerContainer, permission);
        } else {
            flagPermValues.worldsNullable.forEach(worldName -> {
                final String worldNameLower = worldName.toLowerCase();
                worldNameToPermissionsFlags
                        .computeIfAbsent(worldNameLower, k -> new LandPermissionsFlags(secuboid, worldNameLower))
                        .addPermission(playerContainer, permission);
            });
        }
    }

    private Map.Entry<PlayerContainer, Permission> createPcPermissionNullable(final String fileName,
            final String playerContainerName, final String permissionName, final boolean value,
            final boolean inheritable) throws WorldConfigException {
        final PlayerContainer playerContainer = secuboid.getNewInstance()
                .getPlayerContainerFromFileFormat(playerContainerName);
        if (playerContainer == null) {
            throw new WorldConfigException(
                    String.format("In file %s, invalid playercontainer: \"%s\"", fileName, playerContainerName));
        }
        final PermissionType permissionType = secuboid.getPermissionsFlags().getPermissionType(permissionName);
        final Permission permission = secuboid.getPermissionsFlags().newPermission(permissionType, value, inheritable);
        return new AbstractMap.SimpleImmutableEntry<>(playerContainer, permission);
    }

    private final void loadFlags(final FileType fileType, final FlagPermValues flagPermValues) {
        for (final String flagName : flagPermValues.flagsNullable) {
            try {
                final Flag flag = createFlagNullable(fileType.fileName, flagName, flagPermValues.valueNullable,
                        flagPermValues.inheritableOpt.orElse(Boolean.TRUE));
                if (flag == null) {
                    throw new WorldConfigException(
                            String.format("In file %s, unable to load flag \"%s\" with value \"%s\"", fileType.fileName,
                                    flagName, flagPermValues.valueNullable));
                }
                if (fileType == FileType.LAND_DEFAULT) {
                    loadFlagLandDefault(flagPermValues, flag);
                } else {
                    loadFlagWorldConfig(flagPermValues, flag);
                }
            } catch (final WorldConfigException e) {
                log.severe(e.getMessage());
            }
        }
    }

    private void loadFlagLandDefault(final FlagPermValues flagPermValues, final Flag flag) {
        if (flagPermValues.typesNullable == null || flagPermValues.typesNullable.isEmpty()) {
            defaultPermissionsFlags.addFlag(flag);
        } else {
            flagPermValues.typesNullable.forEach(typeName -> {
                final String typeNameLower = typeName.toLowerCase();
                final Type type = secuboid.getTypes().addOrGetType(typeNameLower);
                typeToDefaultPermissionsFlags.computeIfAbsent(type, k -> new LandPermissionsFlags(secuboid))
                        .addFlag(flag);
            });
        }
    }

    private void loadFlagWorldConfig(final FlagPermValues flagPermValues, final Flag flag) {
        if (flagPermValues.worldsNullable == null || flagPermValues.worldsNullable.isEmpty()) {
            globalPermissionsFlags.addFlag(flag);
        } else {
            flagPermValues.worldsNullable.forEach(worldName -> {
                final String worldNameLower = worldName.toLowerCase();
                worldNameToPermissionsFlags
                        .computeIfAbsent(worldNameLower, k -> new LandPermissionsFlags(secuboid, worldNameLower))
                        .addFlag(flag);
            });
        }
    }

    private Flag createFlagNullable(final String fileName, final String flagName, final Object value,
            final boolean inheritable) throws WorldConfigException {
        final FlagType flagType = secuboid.getPermissionsFlags().getFlagType(flagName);
        if (!flagType.getDefaultValue().getClass().isInstance(value)) {
            throw new WorldConfigException(
                    String.format("In file %s, invalid value \"%s\" for flag: \"%s\"", fileName, value, flagName));
        }
        return secuboid.getPermissionsFlags().newFlag(flagType, value, inheritable);
    }

    private enum ParameterType {
        PERMISSION, FLAG;
    }

    private enum FileType {
        LAND_DEFAULT("landdefault.yml"), WORLD_CONFIG("worldconfig.yml");

        final String fileName;

        private FileType(final String fileName) {
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
        Optional<Boolean> inheritableOpt = Optional.empty();

        boolean verifyForFlags() {
            return flagsNullable != null && !flagsNullable.isEmpty() && valueNullable != null;

        }

        boolean verifyForPermissions() {
            if (permissionsNullable == null || playerContainersNullable == null || valueNullable == null) {
                return false;
            }
            if (permissionsNullable.isEmpty() || playerContainersNullable.isEmpty()
                    || !(valueNullable instanceof Boolean)) {
                return false;
            }
            return true;
        }
    }

    private static class WorldConfigException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        WorldConfigException(final String message) {
            super(message);
        }

        WorldConfigException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
