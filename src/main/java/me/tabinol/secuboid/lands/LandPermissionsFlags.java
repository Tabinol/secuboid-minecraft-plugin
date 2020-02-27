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
package me.tabinol.secuboid.lands;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerContainerAddNoEnterEvent;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.storage.SavableParameter;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;

/**
 * The class for permissions and flags access from a land
 *
 * @author tabinol
 */
public final class LandPermissionsFlags {
    private final Secuboid secuboid;
    private final Land landNullable;
    private final String worldNameNullable;
    private final PermFlagType permFlagType;

    /**
     * The permissions.
     */
    private final Map<PlayerContainer, Map<PermissionType, Permission>> permissions;

    /**
     * The flags.
     */
    private final Map<FlagType, Flag> flags;

    public enum PermFlagType {
        LAND, DEFAULT_CONFIG, WORLD_CONFIG
    }

    /**
     * Constructor for land default values.
     * 
     * @param secuboid the secuboid plugin
     */
    public LandPermissionsFlags(final Secuboid secuboid) {
        this.permFlagType = PermFlagType.DEFAULT_CONFIG;
        this.secuboid = secuboid;
        this.landNullable = null;
        this.worldNameNullable = null;
        permissions = new TreeMap<>();
        flags = new TreeMap<>();
    }

    /**
     * Constructor for world configuration,
     * 
     * @param secuboid          the secuboid plugin
     * @param worldNameNullable the world name nullabe
     */
    public LandPermissionsFlags(final Secuboid secuboid, final String worldNameNullable) {
        this.permFlagType = PermFlagType.WORLD_CONFIG;
        this.secuboid = secuboid;
        this.landNullable = null;
        this.worldNameNullable = worldNameNullable;
        permissions = new TreeMap<>();
        flags = new TreeMap<>();
    }

    /**
     * Constructor for land permissions flags
     * 
     * @param secuboid
     * @param worldName
     */
    LandPermissionsFlags(final Secuboid secuboid, final Land land, final String worldName) {
        this.permFlagType = PermFlagType.LAND;
        this.secuboid = secuboid;
        this.landNullable = land;
        this.worldNameNullable = worldName;
        permissions = new TreeMap<>();
        flags = new TreeMap<>();
    }

    public PermFlagType getPermFlagsType() {
        return permFlagType;
    }

    /**
     * Sets the land default values. This method is called from land and does not
     * auto save.
     */
    public void setDefault() {
        setDefault(SaveOn.BOTH);
    }

    void setDefault(final SaveOn saveOn) {
        // Remove all flags
        flags.clear();
        doSave(SaveActionEnum.LAND_FLAG_REMOVE_ALL, saveOn);
        // Remove all permissions
        permissions.clear();
        doSave(SaveActionEnum.LAND_PERMISSION_REMOVE_ALL, saveOn);
    }

    private void doSave(final SaveActionEnum SaveActionEnum, final SaveOn saveOn,
            final SavableParameter... savableParameters) {
        if (landNullable != null && landNullable.getAutoSave()) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum, saveOn, Optional.of(landNullable),
                    savableParameters);
        }
    }

    /**
     * Gets the land associated to the permissions flags if exists.
     * 
     * @return the land or null
     */
    public Land getLandNullable() {
        return landNullable;
    }

    /**
     * Gets the world name or null if this is a configuration not associated to a
     * world.
     * 
     * @return the world name
     */
    public String getWorldNameNullable() {
        return worldNameNullable;
    }

    /**
     * Checks if is banned. Check with the land, or false if it is outside.
     *
     * @param player the player
     * @return true, if is banned
     */
    public boolean isBanned(final Player player) {
        if (landNullable != null) {
            return landNullable.isBanned(player);
        }
        return false;
    }

    /**
     * Copy permissions and falgs to an other LandPermissionsFlags instance wihout
     * override.
     *
     * @param desPermissionsFlags the destination instance
     */
    public void copyPermsFlagsToWithoutOverride(final LandPermissionsFlags desPermissionsFlags) {

        // copy permissions
        permissions.forEach((playerContainer, permTypeToPerm) -> desPermissionsFlags.permissions
                .compute(playerContainer, (k, desPermTypeToPermV) -> {
                    final Map<PermissionType, Permission> desPermTypeToPerm = desPermTypeToPermV != null
                            ? desPermTypeToPermV
                            : new TreeMap<>();
                    permTypeToPerm.forEach((type, perm) -> desPermTypeToPerm.putIfAbsent(type, perm.copyOf()));
                    return desPermTypeToPerm;
                }));

        // copy flags
        flags.forEach((flagType, flag) -> desPermissionsFlags.flags.computeIfAbsent(flagType, k -> flag.copyOf()));
    }

    private LandPermissionsFlags getPermsFlagsParentNullable(final LandPermissionsFlags originLandPermFlags) {
        // From World, return null
        if (permFlagType == PermFlagType.WORLD_CONFIG) {
            return null;
        }

        Land parentNullable = null;
        // Return parent if exist
        if (permFlagType == PermFlagType.DEFAULT_CONFIG && originLandPermFlags.landNullable != null) {
            parentNullable = originLandPermFlags.landNullable.getParent();
        } else if (landNullable != null) {
            parentNullable = landNullable.getParent();
        }
        if (parentNullable != null) {
            return parentNullable.getPermissionsFlags();
        }

        // Return world
        final String originWorldNameNullable = originLandPermFlags.getWorldNameNullable();
        if (originWorldNameNullable != null) {
            return secuboid.getLands().getOutsideLandPermissionsFlags(originWorldNameNullable);
        }

        // Ask from world config (impossible?)
        return null;
    }

    /**
     * Adds the permission.
     *
     * @param pc   the pc
     * @param perm the perm
     */
    public void addPermission(final PlayerContainer pc, final Permission perm) {

        Map<PermissionType, Permission> permPlayer;

        if (!permissions.containsKey(pc)) {
            permPlayer = new TreeMap<>();
            permissions.put(pc, permPlayer);
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave(SaveActionEnum.LAND_PERMISSION_SAVE, SaveOn.BOTH, pc, perm);

        if (landNullable != null) {
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()) {

                // Start Event for kick
                secuboid.getServer().getPluginManager().callEvent(new PlayerContainerAddNoEnterEvent(landNullable, pc));
            }

            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.PERMISSION_SET, perm));
        }
    }

    /**
     * Removes the permission.
     *
     * @param pc       the pc
     * @param permType the perm type
     * @return true, if successful
     */
    public boolean removePermission(final PlayerContainer pc, final PermissionType permType) {
        if (!permissions.containsKey(pc)) {
            return false;
        }

        final Map<PermissionType, Permission> permPlayer = permissions.get(pc);
        final Permission perm = permPlayer.remove(permType);

        if (perm == null) {
            return false;
        }

        // remove key for PC if it is empty
        if (permPlayer.isEmpty()) {
            permissions.remove(pc);
        }

        doSave(SaveActionEnum.LAND_PERMISSION_REMOVE, SaveOn.BOTH, pc, perm);

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.PERMISSION_UNSET, perm));
        }

        return true;
    }

    /**
     * Remove all permissions for this type from every player containers. This
     * command does not save.
     * 
     * @param permissionType
     * @param saveOn
     */
    void removeAllPermissionsType(final PermissionType permissionType, final SaveOn saveOn) {
        for (final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> entry : permissions.entrySet()) {
            final PlayerContainer pc = entry.getKey();
            final Map<PermissionType, Permission> typeToPermission = entry.getValue();
            final Permission perm = typeToPermission.remove(permissionType);
            if (perm != null) {
                doSave(SaveActionEnum.LAND_PERMISSION_REMOVE, saveOn, pc, perm);
            }
        }
    }

    /**
     * Gets the sets the pc have permission.
     *
     * @return the sets the pc have permission
     */
    public final Set<PlayerContainer> getSetPCHavePermission() {

        return permissions.keySet();
    }

    /**
     * Gets the permissions for pc.
     *
     * @param pc the pc
     * @return the permissions for pc
     */
    public final Collection<Permission> getPermissionsForPC(final PlayerContainer pc) {

        return permissions.get(pc).values();
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt     the pt
     * @return the optional boolean
     */
    public boolean checkPermissionAndInherit(final Player player, final PermissionType pt) {
        final Optional<Boolean> resultOpt = checkPermissionAndInherit(player, pt, false, this);
        return resultOpt.orElse(pt.getDefaultValue());
    }

    /**
     * Check land permission and inherit.
     *
     * @param player                 the player
     * @param pt                     the pt
     * @param onlyInherit            the only inherit
     * @param originPermissionsFlags the origin land (or ...) permissions flags
     * @return the optional boolean
     */
    private Optional<Boolean> checkPermissionAndInherit(final Player player, final PermissionType pt,
            final boolean onlyInherit, final LandPermissionsFlags originPermissionsFlags) {

        for (final PlayerContainerType pcType : PlayerContainerType.values()) {
            final Optional<Boolean> permValueOpt = getPermission(pcType, player, pt, onlyInherit);
            if (permValueOpt.isPresent()) {
                return permValueOpt;
            }
        }

        final LandPermissionsFlags permsFlagsParentNullable = getPermsFlagsParentNullable(originPermissionsFlags);
        if (permsFlagsParentNullable != null) {
            return permsFlagsParentNullable.checkPermissionAndInherit(player, pt, true, originPermissionsFlags);
        }

        return Optional.empty();
    }

    /**
     * Gets the permission.
     *
     * @param pcType                 the player container to check
     * @param player                 the player
     * @param pt                     the pt
     * @param onlyInherit            the only inherit
     * @param originPermissionsFlags the origin land (or ...) permissions flags
     * @return the optional boolean
     */
    private Optional<Boolean> getPermission(final PlayerContainerType pcType, final Player player,
            final PermissionType pt, final boolean onlyInherit) {
        Optional<Boolean> resultOpt;

        for (final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry : permissions
                .entrySet()) {
            resultOpt = permissionSingleCheck(pcType, permissionEntry, player, pt, onlyInherit);
            if (resultOpt.isPresent()) {
                return resultOpt;
            }
        }

        // Check default configuration
        LandPermissionsFlags defaultPermissionsFlags;
        if (landNullable != null
                && (defaultPermissionsFlags = secuboid.getLands().getDefaultConf(landNullable.getType())) != null) {
            for (final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry : defaultPermissionsFlags.permissions
                    .entrySet()) {
                resultOpt = permissionSingleCheck(pcType, permissionEntry, player, pt, onlyInherit);
                if (resultOpt.isPresent()) {
                    return resultOpt;
                }
            }
        }

        return Optional.empty();
    }

    private Optional<Boolean> permissionSingleCheck(final PlayerContainerType pcType,
            final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry, final Player player,
            final PermissionType pt, final boolean onlyInherit) {
        final PlayerContainer playerContainer = permissionEntry.getKey();
        final Map<PermissionType, Permission> permissionTypeToPermission = permissionEntry.getValue();
        if (pcType != playerContainer.getContainerType()) {
            return Optional.empty();
        }

        // Prevents infinite loop
        if ((pt == PermissionList.LAND_OWNER.getPermissionType()
                || pt == PermissionList.LAND_TENANT.getPermissionType()) && playerContainer.isLandRelative()) {
            return Optional.empty();
        }

        final boolean value = playerContainer.hasAccess(player, this);
        if (value) {
            Permission perm = permissionTypeToPermission.get(pt);

            // take the parent if the permission does not exist
            if (perm == null && pt.hasParent()) {
                perm = permissionTypeToPermission.get(pt.getParent());
            }

            if (perm != null && (!onlyInherit || perm.isInheritable())) {
                return Optional.of(perm.getValue());
            }
        }

        return Optional.empty();
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(final Flag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave(SaveActionEnum.LAND_FLAG_SAVE, SaveOn.BOTH, flag);

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager()
                    .callEvent(new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.FLAG_SET, flag));
        }
    }

    /**
     * Removes the flag.
     *
     * @param flagType the flag type
     * @return true, if successful
     */
    public boolean removeFlag(final FlagType flagType) {

        final Flag flag = flags.remove(flagType);

        if (flag == null) {
            return false;
        }
        doSave(SaveActionEnum.LAND_FLAG_REMOVE, SaveOn.BOTH, flag);

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager()
                    .callEvent(new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.FLAG_UNSET, flag));
        }

        return true;
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft the ft
     * @return the land flag value
     */
    public FlagValue getFlagAndInherit(final FlagType ft) {
        return getFlagAndInherit(ft, false, this);
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft                     the ft
     * @param onlyInherit            the only inherit
     * @param originPermissionsFlags the origin permissions flags
     * @return the land flag value
     */
    private FlagValue getFlagAndInherit(final FlagType ft, final boolean onlyInherit,
            final LandPermissionsFlags originPermissionsFlags) {

        final FlagValue flagValue;
        if ((flagValue = getFlagNullable(ft, onlyInherit)) != null) {
            return flagValue;
        }

        final LandPermissionsFlags permsFlagsParent = getPermsFlagsParentNullable(originPermissionsFlags);
        if (permsFlagsParent != null) {
            return permsFlagsParent.getFlagAndInherit(ft, true, originPermissionsFlags);
        }

        return ft.getDefaultValue();
    }

    /**
     * Gets the flags.
     *
     * @return the flags value or default
     */
    public Collection<Flag> getFlags() {
        return flags.values();
    }

    /**
     * Gets the flag no inherit.
     *
     * @param ft the ft
     * @return the flag value or default
     */
    public FlagValue getFlagNoInherit(final FlagType ft) {
        final FlagValue valueNullable = getFlagNullable(ft, false);

        if (valueNullable != null) {
            return valueNullable;
        } else {
            return ft.getDefaultValue();
        }
    }

    /**
     * Gets the flag.
     *
     * @param ft          the ft
     * @param onlyInherit the only inherit
     * @return the flag value
     */
    private FlagValue getFlagNullable(final FlagType ft, final boolean onlyInherit) {
        Flag flag = flags.get(ft);
        if (flag != null && (!onlyInherit || flag.isInheritable())) {
            return flag.getValue();
        }

        // Check default configuration
        LandPermissionsFlags defaultPermissionsFlags;
        if (landNullable != null
                && (defaultPermissionsFlags = secuboid.getLands().getDefaultConf(landNullable.getType())) != null) {
            flag = defaultPermissionsFlags.flags.get(ft);
            if (flag != null && (!onlyInherit || flag.isInheritable())) {
                return flag.getValue();
            }
        }

        return null;
    }
}
