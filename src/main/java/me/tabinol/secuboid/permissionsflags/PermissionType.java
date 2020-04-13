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

/**
 * The Class PermissionType.
 */
public class PermissionType extends PermissionsFlagsType {

    /**
     * The default value.
     */
    private boolean defaultValue;

    /**
     * If the permission has a parent
     */
    private final PermissionType parent;

    /**
     * Instantiates a new permission type with parent.
     *
     * @param permissionName the permission name
     * @param defaultValue   the default value
     * @param parent         the parent permission (or null)
     */
    PermissionType(String permissionName, boolean defaultValue, PermissionType parent) {
        super(permissionName);
        this.defaultValue = defaultValue;
        this.parent = parent;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public boolean getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets if there is a parent
     *
     * @return true if there is a parent
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Gets the parent permission type
     *
     * @return the parent permission type
     */
    public PermissionType getParent() {
        return parent;
    }
}