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
package me.tabinol.secuboid.permissionsflags;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.Material;

/**
 * The Class Parameters.
 *
 * @author Tabinol
 */
public class PermissionsFlags {

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
    public PermissionsFlags(Secuboid secuboid) {

        this.secuboid = secuboid;
        permissions = new TreeMap<String, PermissionType>();
        flags = new TreeMap<String, FlagType>();
        unRegisteredFlags = new ArrayList<Flag>();

        // Add flags and permissions
        for (PermissionList permissionList : PermissionList.values()) {
            if (permissionList.getParent() != null) {
                permissionList.setPermissionType(registerPermissionType(permissionList.name(),
                        permissionList.baseValue, getPermissionType(permissionList.getParent())));
            } else {
                permissionList.setPermissionType(registerPermissionType(permissionList.name(),
                        permissionList.baseValue));
            }
        }
        for (FlagList flagList : FlagList.values()) {
            flagList.setFlagType(registerFlagType(flagList.name(), flagList.baseValue));
        }
        // Add special permissions (PLACE_XXX and DESTROY_XXX, NOPLACE_XXX, NODESTROY_XXX)
        specialPermMap = new EnumMap<SpecialPermPrefix, Map<Material, PermissionType>>(SpecialPermPrefix.class);

        for (SpecialPermPrefix pref : SpecialPermPrefix.values()) {
            Map<Material, PermissionType> matPerms = new EnumMap<Material, PermissionType>(Material.class);
            for (Material mat : Material.values()) {
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

        Flag flag = new Flag(flagType, value, inheritable);
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
    public final PermissionType registerPermissionType(String permissionName, boolean defaultValue) {
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
    private PermissionType registerPermissionType(String permissionName, boolean defaultValue,
                                                  PermissionType parent) {

        String permissionNameUpper = permissionName.toUpperCase();
        PermissionType permissionType = getPermissionTypeNoValid(permissionNameUpper, parent);
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
    public final FlagType registerFlagType(String flagName,
                                           Object defaultValue) {

        FlagValue flagDefaultValue;

        // Check is default value is raw or is FlagDefaultValue
        if (defaultValue instanceof FlagValue) {
            flagDefaultValue = (FlagValue) defaultValue;
        } else {
            flagDefaultValue = new FlagValue(defaultValue);
        }

        String flagNameUpper = flagName.toUpperCase();
        FlagType flagType = getFlagTypeNoValid(flagNameUpper);
        flagType.setDefaultValue(flagDefaultValue);
        flagType.setRegistered();

        // Update flag registration (for correct type)
        Iterator<Flag> iFlag = unRegisteredFlags.iterator();
        while (iFlag.hasNext()) {
            Flag flag = iFlag.next();
            if (flagType == flag.getFlagType()) {
                String str = flag.getValue().getValueString();
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
    public final PermissionType getPermissionType(String permissionName) {

        PermissionType pt = permissions.get(permissionName);

        if (pt != null && pt.isRegistered()) {
            return pt;
        } else {
            return null;
        }
    }

    /**
     * Gets the flag type.
     *
     * @param flagName the flag name
     * @return the flag type
     */
    public final FlagType getFlagType(String flagName) {

        FlagType ft = flags.get(flagName);

        if (ft != null && ft.isRegistered()) {
            return ft;
        } else {
            return null;
        }
    }

    /**
     * Gets the permission type no valid.
     *
     * @param permissionName the permission name
     * @return the permission type no valid
     */
    public final PermissionType getPermissionTypeNoValid(String permissionName) {
        return getPermissionTypeNoValid(permissionName, null);
    }

    /**
     * Gets the permission type with no validation.
     *
     * @param permissionName the permission name
     * @param parent         the parent permission (or null)
     * @return the permission type no valid
     */
    public final PermissionType getPermissionTypeNoValid(String permissionName, PermissionType parent) {

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
    public final FlagType getFlagTypeNoValid(String flagName) {

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
    public final PermissionType getSpecialPermission(SpecialPermPrefix prefix, Material mat) {

        Map<Material, PermissionType> matPerms = specialPermMap.get(prefix);

        if (matPerms == null) {
            return null;
        }

        return matPerms.get(mat);
    }
}
