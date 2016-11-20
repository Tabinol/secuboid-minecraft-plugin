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
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import org.bukkit.entity.Player;

/**
 * The class for permissions and flags access from a land
 *
 * @author tabinol
 */
public class LandPermissionsFlags {

    private final Secuboid secuboid;
    private final Land land;
    private final RealLand realLand;

    /**
     * The permissions.
     */
    private TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>> permissions;

    /**
     * The flags.
     */
    private TreeMap<FlagType, Flag> flags;

    LandPermissionsFlags(Secuboid secuboid, Land land) {
        this.secuboid = secuboid;
        this.land = land;
        realLand = land.isRealLand() ? (RealLand) land : null;
        permissions = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        flags = new TreeMap<FlagType, Flag>();
    }

    /**
     * Sets the land default values. This methode is called from RealLand and does not auto save.
     */
    void setDefault() {
        permissions = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        flags = new TreeMap<FlagType, Flag>();
    }

    private void doSave() {
        if (land.isRealLand()) {
            realLand.doSave();
        }
    }

    /**
     * Copy permissions and lands to an other LandPermissionsFlags instance.
     *
     * @param desPermissionsFlags the destination instance
     */
    public void copyPermsFlagsTo(LandPermissionsFlags desPermissionsFlags) {

        // copy permissions
        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> pcEntry : permissions.entrySet()) {

            TreeMap<PermissionType, Permission> perms = new TreeMap<PermissionType, Permission>();
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

    /**
     * Adds the permission.
     *
     * @param pc   the pc
     * @param perm the perm
     */
    public void addPermission(PlayerContainer pc, Permission perm) {

        TreeMap<PermissionType, Permission> permPlayer;

        if (realLand != null) {
            pc.setLand(realLand);
        }

        if (!permissions.containsKey(pc)) {
            permPlayer = new TreeMap<PermissionType, Permission>();
            permissions.put(pc, permPlayer);
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave();

        if (land.isRealLand()) {
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()) {

                // Start Event for kick
                secuboid.getServer().getPluginManager().callEvent(
                        new PlayerContainerAddNoEnterEvent(realLand, pc));
            }

            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(realLand, LandModifyEvent.LandModifyReason.PERMISSION_SET, perm));
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

        TreeMap<PermissionType, Permission> permPlayer;
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

        if (land.isRealLand()) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(realLand, LandModifyEvent.LandModifyReason.PERMISSION_UNSET, perm));
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
        Boolean result = checkPermissionAndInherit(player, pt, false);
        return result == null ? false : result;
    }

    /**
     * Check land permission and inherit.
     *
     * @param player      the player
     * @param pt          the pt
     * @param onlyInherit the only inherit
     * @return the boolean
     */
    private Boolean checkPermissionAndInherit(Player player, PermissionType pt, boolean onlyInherit) {

        Boolean permValue;

        if ((permValue = getPermission(player, pt, onlyInherit)) != null) {
            return permValue;
        } else if (land.isRealLand()) {
            if (realLand.getParent() != null) {
                return realLand.getParent().getPermissionsFlags().checkPermissionAndInherit(player, pt, true);
            } else {
                return secuboid.getLands().getPermissionInWorld(realLand.getWorldName(), player, pt, true);
            }
        }

        return null;
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft the ft
     * @return the land flag value
     */
    public FlagValue getFlagAndInherit(FlagType ft) {

        return getFlagAndInherit(ft, true);
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft          the ft
     * @param onlyInherit the only inherit
     * @return the land flag value
     */
    private FlagValue getFlagAndInherit(FlagType ft, boolean onlyInherit) {

        FlagValue flagValue;

        if ((flagValue = getFlag(ft, onlyInherit)) != null) {
            return flagValue;
        } else if (land.isRealLand()) {
            if (realLand.getParent() != null) {
                return realLand.getParent().getPermissionsFlags().getFlagAndInherit(ft, true);
            } else {
                return secuboid.getLands().getFlagInWorld(realLand.getWorldName(), ft, true);
            }
        }

        return null;
    }

    /**
     * Gets the permission.
     *
     * @param player      the player
     * @param pt          the pt
     * @param onlyInherit the only inherit
     * @return the permission
     */
    Boolean getPermission(Player player, PermissionType pt, boolean onlyInherit) {
        return getPermission(player, pt, onlyInherit, null);
    }

    // Land parameter is only to paste to default parameters for a land
    private Boolean getPermission(Player player, PermissionType pt, boolean onlyInherit, Land land) {

        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            boolean value;
            if (land.isRealLand()) {
                value = permissionEntry.getKey().hasAccess(player, (RealLand) land);
            } else {
                value = permissionEntry.getKey().hasAccess(player);
            }
            if (value) {
                Permission perm = permissionEntry.getValue().get(pt);

                // take the parent if the permission does not exist
                if (perm == null && pt.hasParent()) {
                    perm = permissionEntry.getValue().get(pt.getParent());
                }

                if (perm != null) {
                    secuboid.getLog().write("Container: " + permissionEntry.getKey().toString() + ", "
                            + "PermissionType: " + perm.getPermType() + ", Value: " + perm.getValue() + ", Inheritable: " + perm.isInheritable());
                    if (!onlyInherit || perm.isInheritable()) {
                        return perm.getValue();
                    }
                }
            }
        }

        // Check in default permissions
        if (land.isRealLand()) {
            return secuboid.getLands().getDefaultConf(realLand.getType()).getPermissionsFlags().getPermission(player, pt, false, land);
        }

        return null;
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(Flag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();

        if (land.isRealLand()) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent(realLand, LandModifyEvent.LandModifyReason.FLAG_SET, flag));
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

        if (land.isRealLand()) {
            // Start Event
            secuboid.getServer().getPluginManager().callEvent(
                    new LandModifyEvent((RealLand) land, LandModifyEvent.LandModifyReason.FLAG_UNSET, flag));
        }

        return true;
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
    FlagValue getFlag(FlagType ft, boolean onlyInherit) {

        Flag flag = flags.get(ft);
        if (flag != null) {
            secuboid.getLog().write("Flag: " + flag.toString());

            if (!onlyInherit || flag.isInheritable()) {
                return flag.getValue();
            }
        }

        // Check in default flags
        if (!onlyInherit && land.isRealLand()) {

            return (secuboid.getLands()).getDefaultConf(((RealLand) land).getType()).getPermissionsFlags().getFlag(ft, false);
        }

        return null;
    }
}
