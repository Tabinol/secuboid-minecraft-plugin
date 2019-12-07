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
     * Constructor for land default values
     * 
     * @param secuboid
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
     * Constructor for world configuration
     * 
     * @param secuboid
     * @param worldNameNullable
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
        // Remove all flags
        flags.clear();
        // Remove all permissions
        permissions.clear();
    }

    private void doSave() {
        if (landNullable != null) {
            landNullable.doSave();
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
        permissions.forEach((playerContainer, permTypeToPerm) -> 
            desPermissionsFlags.permissions.compute(playerContainer, (k, desPermTypeToPermV) -> {
                final Map<PermissionType, Permission> desPermTypeToPerm = desPermTypeToPermV != null
                        ? desPermTypeToPermV
                        : new TreeMap<>();
                permTypeToPerm.forEach((type, perm) -> desPermTypeToPerm.putIfAbsent(type, perm.copyOf()));
                return desPermTypeToPerm;
            })
        );

        // copy flags
        flags.forEach((flagType, flag) -> 
            desPermissionsFlags.flags.computeIfAbsent(flagType, k -> flag.copyOf()));
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
        doSave();

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

        Map<PermissionType, Permission> permPlayer;
        Permission perm;

        if (!permissions.containsKey(pc)) {
            return false;
        }
        permPlayer = permissions.get(pc);
        perm = permPlayer.remove(permType);
        if (perm == null) {
            return false;
        }

        // remove key for PC if it is empty
        if (permPlayer.isEmpty()) {
            permissions.remove(pc);
        }

        doSave();

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.PERMISSION_UNSET, perm));
        }

        return true;
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
     * @return the boolean
     */
    public boolean checkPermissionAndInherit(final Player player, final PermissionType pt) {
        final Boolean resultNullable = checkPermissionAndInheritNullable(player, pt, false, this);
        if (resultNullable != null) {
            return resultNullable;
        }
        return pt.getDefaultValue();
    }

    /**
     * Check land permission and inherit.
     *
     * @param player                 the player
     * @param pt                     the pt
     * @param onlyInherit            the only inherit
     * @param originPermissionsFlags the origin land (or ...) permissions flags
     * @return the boolean
     */
    private Boolean checkPermissionAndInheritNullable(final Player player, final PermissionType pt, final boolean onlyInherit,
            final LandPermissionsFlags originPermissionsFlags) {
        Boolean permValueNullable;

        for (final PlayerContainerType pcType : PlayerContainerType.values()) {
            if ((permValueNullable = getPermissionNullable(pcType, player, pt, onlyInherit, originPermissionsFlags)) != null) {
                return permValueNullable;
            }
        }

        final LandPermissionsFlags permsFlagsParentNullable = getPermsFlagsParentNullable(originPermissionsFlags);
        if (permsFlagsParentNullable != null) {
            return permsFlagsParentNullable.checkPermissionAndInheritNullable(player, pt, true, originPermissionsFlags);
        }

        return null;
    }

    /**
     * Gets the permission.
     *
     * @param pcType                 the player container to check
     * @param player                 the player
     * @param pt                     the pt
     * @param onlyInherit            the only inherit
     * @param originPermissionsFlags the origin land (or ...) permissions flags
     * @return the permission
     */
    private Boolean getPermissionNullable(final PlayerContainerType pcType, final Player player, final PermissionType pt, final boolean onlyInherit,
            final LandPermissionsFlags originPermissionsFlags) {
        Boolean result;

        for (final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            result = permissionSingleCheckNullable(pcType, permissionEntry, player, pt, onlyInherit, originPermissionsFlags);
            if (result != null) {
                return result;
            }
        }

        // Check default configuration
        LandPermissionsFlags defaultPermissionsFlags;
        if (landNullable != null
                && (defaultPermissionsFlags = secuboid.getLands().getDefaultConf(landNullable.getType())) != null) {
            for (final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry : defaultPermissionsFlags.permissions
                    .entrySet()) {
                result = permissionSingleCheckNullable(pcType, permissionEntry, player, pt, onlyInherit,
                        originPermissionsFlags);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Boolean permissionSingleCheckNullable(final PlayerContainerType pcType,
            final Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry, final Player player,
            final PermissionType pt, final boolean onlyInherit, final LandPermissionsFlags originPermissionsFlags) {
        if (pcType != permissionEntry.getKey().getContainerType()) {
            return null;
        }

        boolean value;
        value = permissionEntry.getKey().hasAccess(player, landNullable, originPermissionsFlags);
        if (value) {
            Permission perm = permissionEntry.getValue().get(pt);

            // take the parent if the permission does not exist
            if (perm == null && pt.hasParent()) {
                perm = permissionEntry.getValue().get(pt.getParent());
            }

            if (perm != null && (!onlyInherit || perm.isInheritable())) {
                return perm.getValue();
            }
        }

        return null;
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(final Flag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();

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
        doSave();

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
    private FlagValue getFlagAndInherit(final FlagType ft, final boolean onlyInherit, final LandPermissionsFlags originPermissionsFlags) {

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
    private FlagValue getFlagNullable(FlagType ft, boolean onlyInherit) {
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
