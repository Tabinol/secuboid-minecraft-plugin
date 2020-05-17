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

import org.bukkit.ChatColor;

import me.tabinol.secuboid.storage.SavableParameter;

/**
 * The Class Permission.
 */
public final class Permission implements SavableParameter {

    /**
     * The perm type.
     */
    private final PermissionType permType;

    /**
     * The value.
     */
    private final boolean value;

    /**
     * The Inheritable.
     */
    private final boolean inheritable;

    /**
     * Instantiates a new permission.
     *
     * @param permType    the perm type
     * @param value       the value
     * @param inheritable the inheritable
     */
    Permission(final PermissionType permType, final boolean value, final boolean inheritable) {
        this.permType = permType;
        this.value = value;
        this.inheritable = inheritable;
    }

    public Permission copyOf() {
        return new Permission(permType, value, inheritable);
    }

    /**
     * Gets the perm type.
     *
     * @return the perm type
     */
    public PermissionType getPermType() {
        return permType;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Gets the value print.
     *
     * @return the value print
     */
    public final String getValuePrint() {
        if (value) {
            return "" + ChatColor.GREEN + true;
        } else {
            return "" + ChatColor.RED + false;
        }
    }

    /**
     * Checks if is inheritable.
     *
     * @return true, if is inheritable
     */
    public boolean isInheritable() {
        return inheritable;
    }

    public String toFileFormat() {
        return permType.toString() + ":" + value + ":" + inheritable;
    }
}
