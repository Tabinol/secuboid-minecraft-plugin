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
import org.yaml.snakeyaml.parser.ParserException;

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

    private static final String[] OLD_KEYS = new String[] { "_Global_", "ContainerPermissions", "ContainerFlags" };

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
    private boolean isOldVersion;

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
        isOldVersion = false;
        globalPermissionsFlags.setDefault();
        worldNameToPermissionsFlags.clear();
        defaultPermissionsFlags.setDefault();
        typeToDefaultPermissionsFlags.clear();
        Arrays.stream(FileType.values()).forEach(this::loadData);

        if (isOldVersion) {
            log.warning("You are using the old unsupported version of worldconfig.yml and landdefault.yml.");
            log.warning("Rename those files and restart the server to create the new configuration.");
        }

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

    private void loadData(final FileType fileType) {
        final String fileName = fileType.fileName;

        // Create files (if not exist) and load
        final File configFileFolder = secuboid.getDataFolder();
        if (!new File(configFileFolder, fileName).exists()) {
            secuboid.saveResource(fileName, false);
        }

        try {
            final InputStream inputStream = new FileInputStream(new File(configFileFolder, fileName));
            loadDataYml(inputStream, fileType);
        } catch (final FileNotFoundException e) {
            log.log(Level.SEVERE, String.format("Unable to load %s!", fileName), e);
            return;
        }
    }

    @SuppressWarnings("unchecked")
    void loadDataYml(final InputStream inputStream, final FileType fileType) {
        final String fileName = fileType.fileName;
        final Yaml yaml = new Yaml();
        final Object rootObj;
        try {
            rootObj = yaml.load(inputStream);
        } catch (ParserException e) {
            log.log(Level.SEVERE, String.format("Yaml error in %s", fileName), e);
            return;
        }
        if (!(rootObj instanceof Map)) {
            log.log(Level.SEVERE, () -> String.format("The file format is incorrect: %s", fileName));
            return;
        }
        final Map<String, Object> root = (Map<String, Object>) rootObj;
        for (final Map.Entry<String, Object> entry : root.entrySet()) {
            try {
                final String keyName = entry.getKey();
                final Object valueObj = entry.getValue();
                final ParameterType parameterType = getParameterTypeFromString(fileName, keyName);

                // Check if the key is a list
                if (valueObj != null && !(valueObj instanceof List)) {
                    throw new WorldConfigException(
                            String.format("In file %s, key \"%s\" must be a list: - ...", fileName, keyName));
                }

                // Load permissions and flags
                loadFlagPerm(fileType, parameterType, keyName, (List<Object>) valueObj);
            } catch (final WorldConfigException ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    final ParameterType getParameterTypeFromString(final String fileName, final String keyName)
            throws WorldConfigException {
        final ParameterType parameterType;
        try {
            parameterType = ParameterType.valueOf(keyName.toUpperCase());
        } catch (final IllegalArgumentException e) {
            if (Arrays.stream(OLD_KEYS).anyMatch(keyName::equalsIgnoreCase)) {
                isOldVersion = true;
            }
            throw new WorldConfigException(String.format("In file %s, invalid tag name: \"%s\"", fileName, keyName), e);
        }
        return parameterType;
    }

    @SuppressWarnings("unchecked")
    private void loadFlagPerm(final FileType fileType, final ParameterType parameterType, final String rootKey,
            final List<Object> objects) {
        if (objects == null || objects.isEmpty()) {
            // Do not load anything if empty
            return;
        }
        for (final Object keyToValueObj : objects) {
            if (keyToValueObj instanceof Map) {
                loadFlagPermFromKey(fileType, parameterType, rootKey, (Map<String, Object>) keyToValueObj);
            } else {
                log.log(Level.SEVERE,
                        () -> String.format("In file %s, invalid format for permissions", fileType.fileName));
            }
        }
    }

    private void loadFlagPermFromKey(final FileType fileType, final ParameterType parameterType, final String rootKey,
            final Map<String, Object> keyToValue) {
        // Load flags/perms list
        final FlagPermValues flagPermValues = loadFlagPermValues(fileType, rootKey, keyToValue, parameterType);

        if (parameterType == ParameterType.FLAGS) {
            if (!flagPermValues.verifyForFlags()) {
                log.log(Level.SEVERE, () -> String
                        .format("In file %s, a flag must have at least a flag name and a value", fileType.fileName));
                return;
            }
            loadFlags(fileType, flagPermValues);
        }

        if (parameterType == ParameterType.PERMISSIONS) {
            if (!flagPermValues.verifyForPermissions() || !flagPermValues.verifyForPermissionsNotEmpty()) {
                log.log(Level.SEVERE, () -> String.format(
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
            casePlayerContainers(flagPermValues, fileType, rootKey, parameterType, keyName, valueObj);
            break;

        case KEY_PERMISSIONS:
            casePermissions(flagPermValues, fileType, rootKey, parameterType, keyName, valueObj);
            break;

        case KEY_FLAGS:
            caseFlags(flagPermValues, fileType, rootKey, parameterType, keyName, valueObj);
            break;

        case KEY_WORLDS:
            caseWorlds(flagPermValues, fileType, rootKey, keyName, valueObj);
            break;

        case KEY_TYPES:
            caseTypes(flagPermValues, fileType, rootKey, keyName, valueObj);
            break;

        case KEY_VALUE:
            caseValue(flagPermValues, fileType, rootKey, parameterType, keyName, valueObj);
            break;

        case KEY_INHERITABLE:
            caseInheritable(flagPermValues, fileType, rootKey, keyName, valueObj);
            break;

        default:
            log.log(Level.SEVERE, () -> String.format("In file %s, invalid key: \"%s: %s\" for root key \"%s\"",
                    fileName, keyName, Objects.toString(valueObj), rootKey));
        }
    }

    private void casePlayerContainers(final FlagPermValues flagPermValues, final FileType fileType,
            final String rootKey, final ParameterType parameterType, final String keyName, final Object valueObj) {
        if (parameterType == ParameterType.PERMISSIONS) {
            flagPermValues.playerContainersNullable = loadStringList(valueObj);
        } else {
            loadFlagPermErrorMsg(fileType.fileName, keyName, rootKey);
        }
    }

    private void casePermissions(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final ParameterType parameterType, final String keyName, final Object valueObj) {
        if (parameterType == ParameterType.PERMISSIONS) {
            flagPermValues.permissionsNullable = loadStringList(valueObj);
        } else {
            loadFlagPermErrorMsg(fileType.fileName, keyName, rootKey);
        }
    }

    private void caseFlags(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final ParameterType parameterType, final String keyName, final Object valueObj) {
        if (parameterType == ParameterType.FLAGS) {
            flagPermValues.flagsNullable = loadStringList(valueObj);
        } else {
            loadFlagPermErrorMsg(fileType.fileName, keyName, rootKey);
        }
    }

    private void caseWorlds(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final String keyName, final Object valueObj) {
        if (fileType == FileType.WORLD_CONFIG) {
            flagPermValues.worldsNullable = loadStringList(valueObj);
        } else {
            loadFlagPermErrorMsg(fileType.fileName, keyName, rootKey);
        }
    }

    private void caseTypes(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final String keyName, final Object valueObj) {
        if (fileType == FileType.LAND_DEFAULT) {
            flagPermValues.typesNullable = loadStringList(valueObj);
        } else {
            loadFlagPermErrorMsg(fileType.fileName, keyName, rootKey);
        }
    }

    private void caseValue(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final ParameterType parameterType, final String keyName, final Object valueObj) {
        flagPermValues.valueNullable = getBoolean(valueObj).orElse(null);
        if (flagPermValues.valueNullable != null) {
            return;
        }
        if (parameterType == ParameterType.PERMISSIONS) {
            log.log(Level.SEVERE,
                    () -> String.format(
                            "In file %s, the permission value must be true or false: \"%s: %s\" for root key \"%s\"",
                            fileType.fileName, keyName, Objects.toString(valueObj), rootKey));
        }
        flagPermValues.valueNullable = getDouble(valueObj).orElse(null);
        if (flagPermValues.valueNullable != null) {
            return;
        }
        flagPermValues.valueNullable = getStringArray(valueObj).orElse(null);
        if (flagPermValues.valueNullable != null) {
            return;
        }
        flagPermValues.valueNullable = Objects.toString(valueObj);
    }

    private void caseInheritable(final FlagPermValues flagPermValues, final FileType fileType, final String rootKey,
            final String keyName, final Object valueObj) {
        flagPermValues.inheritableOpt = getBoolean(valueObj);
        if (!flagPermValues.inheritableOpt.isPresent()) {
            log.log(Level.SEVERE,
                    () -> String.format("In file %s, inheritable must be true or false: \"%s: %s\" for root key \"%s\"",
                            fileType.fileName, keyName, Objects.toString(valueObj), rootKey));
        }
    }

    private void loadFlagPermErrorMsg(final String fileName, final String keyName, final String rootKey) {
        log.log(Level.SEVERE, () -> String.format("In file %s, invalid tag name: \"%s\" for root key \"%s\"", fileName,
                keyName, rootKey));
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

    @SuppressWarnings("unchecked")
    private Optional<String[]> getStringArray(final Object valueObj) {
        if (valueObj instanceof List) {
            return Optional.of(((List<String>) valueObj).toArray(new String[0]));
        }
        return Optional.empty();
    }

    private Optional<Double> getDouble(final Object valueObj) {
        try {
            return Optional.of(Double.parseDouble(Objects.toString(valueObj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private final void loadPermissions(final FileType fileType, final FlagPermValues flagPermValues) {
        for (final String playerContainerName : flagPermValues.playerContainersNullable) {
            for (final String permissionName : flagPermValues.permissionsNullable) {
                try {
                    loadPermission(fileType, flagPermValues, playerContainerName, permissionName);
                } catch (final WorldConfigException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
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
        final PermissionType permissionType = secuboid.getPermissionsFlags().getPermissionTypeNoValid(permissionName);
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
                log.log(Level.SEVERE, e.getMessage(), e);
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
        FlagType flagType = secuboid.getPermissionsFlags().getFlagType(flagName);
        if (flagType != null) {
            if (!flagType.getDefaultValue().getValue().getClass().isInstance(value)) {
                throw new WorldConfigException(
                        String.format("In file %s, invalid value \"%s\" for flag: \"%s\"", fileName, value, flagName));
            }
            return secuboid.getPermissionsFlags().newFlag(flagType, value, inheritable);
        }
        flagType = secuboid.getPermissionsFlags().getFlagTypeNoValid(flagName);
        return secuboid.getPermissionsFlags().newFlag(flagType, value, inheritable);
    }

    private enum ParameterType {
        PERMISSIONS, FLAGS;
    }

    enum FileType {
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
            return true;
        }

        boolean verifyForPermissionsNotEmpty() {
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
