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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerContainerAddNoEnterEvent;
import me.tabinol.secuboid.permissionsflags.*;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import org.bukkit.entity.Player;

import de.myzelyam.supervanish.events.WorldChangeEvent;

/**
 * The class for permissions and flags access from a land
 *
 * @author tabinol
 */
public final class LandPermissionsFlags {

    private final Secuboid secuboid;
    private final Land landNullable;
    private final String worldNameNullable;

    /**
     * The permissions.
     */
    private final Map<PlayerContainer, Map<PermissionType, Permission>> permissions;

    /**
     * The flags.
     */
    private final Map<FlagType, Flag> flags;

    public LandPermissionsFlags(Secuboid secuboid, Land landNullable, String worldNameNullable) {
        this.secuboid = secuboid;
        this.landNullable = landNullable;
        this.worldNameNullable = worldNameNullable;
        // We use TreeMap because flags and permissions must be ordered.
        permissions = new TreeMap<>();
        flags = new TreeMap<>();
    }

    /**
     * Sets the land default values. This method is called from land and does not auto save.
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
     * @return the land or null
     */
    public Land getLandNullable() {
        return landNullable;
    }

    /**
     * Gets the world name or null if this is a configuration not associated to a world.
     * @return the world name
     */
    public String getWorldNameNullable() {
        return worldNameNullable;
    }

    /**
     * Copy permissions and lands to an other LandPermissionsFlags instance.
     *
     * @param desPermissionsFlags the destination instance
     */
    public void copyPermsFlagsTo(LandPermissionsFlags desPermissionsFlags) {

        // copy permissions
        for (Map.Entry<PlayerContainer, Map<PermissionType, Permission>> pcEntry : permissions.entrySet()) {

            Map<PermissionType, Permission> perms = new TreeMap<>();
            for (Map.Entry<PermissionType, Permission> permEntry : pcEntry.getValue().entrySet()) {
                perms.put(permEntry.getKey(), permEntry.getValue().copyOf());
            }
            desPermissionsFlags.permissions.put(pcEntry.getKey(), perms);
        }

        // copy flags
        for (Map.Entry<FlagType, Flag> flagEntry : flags.entrySet()) {

            desPermissionsFlags.flags.put(flagEntry.getKey(), flagEntry.getValue().copyOf());
        }
    }

    private LandPermissionsFlags getPermsFlagsParent(Land originLand) {

        // From World, return null
        if (land.getLandType() == Land.LandType.WORLD) {
            return null;
        }

        Land parent = null;

        // Return parent if exist
        if (land.getLandType() == Land.LandType.DEFAULT && originLand instanceof RealLand) {
            parent = ((RealLand) originLand).getParent();
        } else if (realLand != null) {
            parent = realLand.getParent();
        }
        if (parent != null) {
            return parent.getPermissionsFlags();
        }

        // Return world
        String worldName = originLand.getWorldName();
        if (worldName != null) {
            return secuboid.getLands().getOutsideArea(worldName).getPermissionsFlags();
        }

        // Demand from world config (impossible?)
        return null;
    }

    /* *********************
     * **** Permissions ****
     * *********************/

    /**
     * Adds the permission.
     *
     * @param pc   the pc
     * @param perm the perm
     */
    public void addPermission(PlayerContainer pc, Permission perm) {

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
                secuboid.getServer().getPluginManager().callEvent(
                        new PlayerContainerAddNoEnterEvent(landNullable, pc));
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
    public boolean removePermission(PlayerContainer pc,
                                    PermissionType permType) {

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
    public final Collection<Permission> getPermissionsForPC(PlayerContainer pc) {

        return permissions.get(pc).values();
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt     the pt
     * @return the boolean
     */
    public boolean checkPermissionAndInherit(Player player, PermissionType pt) {
        for (PlayerContainerType pcType : PlayerContainerType.values()) {
            Boolean result = checkPermissionAndInherit(pcType, player, pt, false, land);
            if (result != null) {
                return result;
            }
        }

        return pt.getDefaultValue();
    }

    /**
     * Check land permission and inherit.
     *
     * @param pcType      the player container to check
     * @param player      the player
     * @param pt          the pt
     * @param onlyInherit the only inherit
     * @param originLand  the origin land
     * @return the boolean
     */
    private Boolean checkPermissionAndInherit(PlayerContainerType pcType, Player player, PermissionType pt,
                                              boolean onlyInherit, Land originLand) {
        Boolean permValue;

        if ((permValue = getPermission(pcType, player, pt, onlyInherit, originLand)) != null) {
            return permValue;
        }

        LandPermissionsFlags permsFlagsParent = getPermsFlagsParent(originLand);
        if (permsFlagsParent != null) {
            return permsFlagsParent.checkPermissionAndInherit(pcType, player, pt, true, originLand);
        }

        return null;
    }

    /**
     * Gets the permission.
     *
     * @param pcType      the player container to check
     * @param player      the player
     * @param pt          the pt
     * @param onlyInherit the only inherit
     * @param originLand  the origin land (if exist)
     * @return the permission
     */
    private Boolean getPermission(PlayerContainerType pcType, Player player, PermissionType pt, boolean onlyInherit,
                                  Land originLand) {
        Boolean result;

        for (Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            result = permissionSingleCheck(pcType, permissionEntry, player, pt, onlyInherit, originLand);
            if (result != null) {
                return result;
            }
        }

        // Check default configuration
        DefaultLand defaultLand;
        if (landNullable != null && (defaultLand = secuboid.getLands().getDefaultConf(landNullable.getType())) != null) {
            for (Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry :
                    defaultLand.getPermissionsFlags().permissions.entrySet()) {
                result = permissionSingleCheck(pcType, permissionEntry, player, pt, onlyInherit, originLand);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Boolean permissionSingleCheck(PlayerContainerType pcType,
                                          Map.Entry<PlayerContainer, Map<PermissionType, Permission>> permissionEntry,
                                          Player player, PermissionType pt, boolean onlyInherit, Land originLand) {
        if (pcType != permissionEntry.getKey().getContainerType()) {
            return null;
        }

        boolean value;
        value = permissionEntry.getKey().hasAccess(player, land, originLand);
        if (value) {
            Permission perm = permissionEntry.getValue().get(pt);

            // take the parent if the permission does not exist
            if (perm == null && pt.hasParent()) {
                perm = permissionEntry.getValue().get(pt.getParent());
            }

            if (perm != null) {
                if (!onlyInherit || perm.isInheritable()) {
                    return perm.getValue();
                }
            }
        }

        return null;
    }

    /* ***************
     * **** Flags ****
     * ***************/

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(Flag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.FLAG_SET, flag));
        }
    }

    /**
     * Removes the flag.
     *
     * @param flagType the flag type
     * @return true, if successful
     */
    public boolean removeFlag(FlagType flagType) {

        Flag flag = flags.remove(flagType);

        if (flag == null) {
            return false;
        }
        doSave();

        if (landNullable != null) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(landNullable, LandModifyEvent.LandModifyReason.FLAG_UNSET, flag));
        }

        return true;
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft the ft
     * @return the land flag value
     */
    public FlagValue getFlagAndInherit(FlagType ft) {

        return getFlagAndInherit(ft, false, land);
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft          the ft
     * @param onlyInherit the only inherit
     * @param originLand  the origin land (if exist)
     * @return the land flag value
     */
    private FlagValue getFlagAndInherit(FlagType ft, boolean onlyInherit, Land originLand) {

        FlagValue flagValue;
        if ((flagValue = getFlag(ft, onlyInherit)) != null) {
            return flagValue;
        }

        LandPermissionsFlags permsFlagsParent = getPermsFlagsParent(originLand);
        if (permsFlagsParent != null) {
            return permsFlagsParent.getFlagAndInherit(ft, true, originLand);
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
    public FlagValue getFlagNoInherit(FlagType ft) {
        FlagValue value = getFlag(ft, false);

        if (value != null) {
            return value;
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
    private FlagValue getFlag(FlagType ft, boolean onlyInherit) {
        Flag flag = flags.get(ft);
        if (flag != null) {
            if (!onlyInherit || flag.isInheritable()) {
                return flag.getValue();
            }
        }

        // Check default configuration
        DefaultLand defaultLand;
        if (landNullable != null && (defaultLand = secuboid.getLands().getDefaultConf(landNullable.getType())) != null) {
            flag = defaultLand.getPermissionsFlags().flags.get(ft);
            if (flag != null) {
                if (!onlyInherit || flag.isInheritable()) {
                    return flag.getValue();
                }
            }
        }

        return null;
    }
}
