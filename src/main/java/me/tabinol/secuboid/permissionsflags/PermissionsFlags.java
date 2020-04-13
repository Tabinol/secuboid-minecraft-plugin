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
package me.tabinol.secuboid.permissionsflags;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.Material;

/**
 * The Class Parameters.
 *
 * @author Tabinol
 */
public final class PermissionsFlags {

    /**
     * Prefix of specials permissions (ex: PLACE_SIGN).
     */
    public enum SpecialPermPrefix {

        /**
         * Place a block
         */
        PLACE,
        /**
         * Prevent place a block
         */
        NOPLACE,
        /**
         * Destroy a block
         */
        DESTROY,
        /**
         * Prevent destroy a block
         */
        NODESTROY
    }

    private final Secuboid secuboid;

    /**
     * The permissions.
     */
    private final TreeMap<String, PermissionType> permissions;

    /**
     * The flags.
     */
    private final TreeMap<String, FlagType> flags;

    /**
     * List of unregistered flags for an update *
     */
    private final List<Flag> unRegisteredFlags;

    /**
     * Special permission Map Prefix-->Material-->PermissionType
     */
    private final Map<SpecialPermPrefix, Map<Material, PermissionType>> specialPermMap;

    /**
     * Instantiates a new parameters.
     *
     * @param secuboid the secuboid instance
     */
    public PermissionsFlags(final Secuboid secuboid) {

        this.secuboid = secuboid;
        permissions = new TreeMap<>();
        flags = new TreeMap<>();
        unRegisteredFlags = new ArrayList<>();

        // Add flags and permissions
        for (final PermissionList permissionList : PermissionList.values()) {
            if (permissionList.getParent() != null) {
                permissionList.setPermissionType(registerPermissionType(permissionList.name(), permissionList.baseValue,
                        getPermissionType(permissionList.getParent())));
            } else {
                permissionList
                        .setPermissionType(registerPermissionType(permissionList.name(), permissionList.baseValue));
            }
        }
        for (final FlagList flagList : FlagList.values()) {
            flagList.setFlagType(registerFlagType(flagList.name(), flagList.baseValue));
        }
        // Add special permissions (PLACE_XXX and DESTROY_XXX, NOPLACE_XXX,
        // NODESTROY_XXX)
        specialPermMap = new EnumMap<>(SpecialPermPrefix.class);

        for (final SpecialPermPrefix pref : SpecialPermPrefix.values()) {
            final Map<Material, PermissionType> matPerms = new EnumMap<>(Material.class);
            for (final Material mat : Material.values()) {
                matPerms.put(mat, registerPermissionType(pref.name() + "_" + mat.name(), false));
            }
            specialPermMap.put(pref, matPerms);
        }
    }

    /**
     * Instantiates a new permission.
     *
     * @param permType    the perm type
     * @param value       the value
     * @param inheritable the inheritable
     * @return the permission
     */
    public Permission newPermission(final PermissionType permType, final boolean value, final boolean inheritable) {
        return new Permission(permType, value, inheritable);
    }

    /**
     * Instantiates a new land flag.
     *
     * @param flagType    the flag type
     * @param value       the value
     * @param inheritable the inheritable
     * @return the flag
     */
    public Flag newFlag(final FlagType flagType, final Object value, final boolean inheritable) {

        final Flag flag = new Flag(flagType, value, inheritable);
        if (!flagType.isRegistered()) {
            unRegisteredFlags.add(flag);
        }

        return flag;
    }

    /**
     * Register permission type.
     *
     * @param permissionName the permission name
     * @param defaultValue   the default value
     * @return the permission type
     */
    public final PermissionType registerPermissionType(final String permissionName, final boolean defaultValue) {
        return registerPermissionType(permissionName, defaultValue, null);
    }

    /**
     * Register permission type.
     *
     * @param permissionName the permission name
     * @param defaultValue   the default value
     * @param parent         the parent permission
     * @return the permission type
     */
    private PermissionType registerPermissionType(final String permissionName, final boolean defaultValue,
            final PermissionType parent) {

        final String permissionNameUpper = permissionName.toUpperCase();
        final PermissionType permissionType = getPermissionTypeNoValid(permissionNameUpper, parent);
        permissionType.setDefaultValue(defaultValue);
        permissionType.setRegistered();

        return permissionType;
    }

    /**
     * Register flag type.
     *
     * @param flagName     the flag name
     * @param defaultValue the default value
     * @return the flag type
     */
    public FlagType registerFlagType(final String flagName, final Object defaultValue) {

        FlagValue flagDefaultValue;

        // Check is default value is raw or is FlagDefaultValue
        if (defaultValue instanceof FlagValue) {
            flagDefaultValue = (FlagValue) defaultValue;
        } else {
            flagDefaultValue = new FlagValue(defaultValue);
        }

        final String flagNameUpper = flagName.toUpperCase();
        final FlagType flagType = getFlagTypeNoValid(flagNameUpper);
        flagType.setDefaultValue(flagDefaultValue);
        flagType.setRegistered();

        // Update flag registration (for correct type)
        final Iterator<Flag> iFlag = unRegisteredFlags.iterator();
        while (iFlag.hasNext()) {
            final Flag flag = iFlag.next();
            if (flagType == flag.getFlagType()) {
                final String str = flag.getValue().getValueString();
                flag.setValue(secuboid.getNewInstance().getFlagValueFromFileFormat(str, flagType));
                iFlag.remove();
            }
        }

        return flagType;
    }

    /**
     * Gets the permission type.
     *
     * @param permissionName the permission name
     * @return the permission type
     */
    public PermissionType getPermissionType(final String permissionName) {

        final PermissionType pt = permissions.get(permissionName);

        if (pt != null && pt.isRegistered()) {
            return pt;
        } else {
            return null;
        }
    }

    /**
     * Gets all permission type manes.
     *
     * @return the permission type names
     */
    public Set<String> getPermissionTypeNames() {
        return permissions.keySet();
    }

    /**
     * Gets the flag type.
     *
     * @param flagName the flag name
     * @return the flag type
     */
    public FlagType getFlagType(final String flagName) {

        final FlagType ft = flags.get(flagName);

        if (ft != null && ft.isRegistered()) {
            return ft;
        } else {
            return null;
        }
    }

    /**
     * Gets all flag type manes.
     *
     * @return the flag type names
     */
    public Set<String> getFlagTypeNames() {
        return flags.keySet();
    }

    /**
     * Gets the permission type no valid.
     *
     * @param permissionName the permission name
     * @return the permission type no valid
     */
    public PermissionType getPermissionTypeNoValid(final String permissionName) {
        return getPermissionTypeNoValid(permissionName, null);
    }

    /**
     * Gets the permission type with no validation.
     *
     * @param permissionName the permission name
     * @param parent         the parent permission (or null)
     * @return the permission type no valid
     */
    public PermissionType getPermissionTypeNoValid(final String permissionName, final PermissionType parent) {

        PermissionType pt = permissions.get(permissionName);

        if (pt == null) {
            pt = new PermissionType(permissionName, false, parent);
            permissions.put(permissionName, pt);
        }

        return pt;
    }

    /**
     * Gets the flag type no valid.
     *
     * @param flagName the flag name
     * @return the flag type no valid
     */
    public FlagType getFlagTypeNoValid(final String flagName) {

        FlagType ft = flags.get(flagName);

        if (ft == null) {
            ft = new FlagType(flagName, "");
            flags.put(flagName, ft);
        }

        return ft;
    }

    /**
     * Gets specials permissions.
     *
     * @param prefix the prefix
     * @param mat    the material
     * @return the permission type
     */
    public PermissionType getSpecialPermission(final SpecialPermPrefix prefix, final Material mat) {

        final Map<Material, PermissionType> matPerms = specialPermMap.get(prefix);

        if (matPerms == null) {
            return null;
        }

        return matPerms.get(mat);
    }
}
