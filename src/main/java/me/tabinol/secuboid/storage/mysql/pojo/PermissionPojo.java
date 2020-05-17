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
package me.tabinol.secuboid.storage.mysql.pojo;

import java.util.UUID;

public final class PermissionPojo {

    private final UUID landUUID;
    private final long playerContainerId;
    private final long permissionId;
    private final boolean value;
    private final boolean inheritance;

    public PermissionPojo(final UUID landUUID, final long playerContainerId, final long permissionId,
            final boolean value, final boolean inheritance) {
        this.landUUID = landUUID;
        this.playerContainerId = playerContainerId;
        this.permissionId = permissionId;
        this.value = value;
        this.inheritance = inheritance;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public long getPlayerContainerId() {
        return this.playerContainerId;
    }

    public long getPermissionId() {
        return this.permissionId;
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean isValue() {
        return this.value;
    }

    public boolean getInheritance() {
        return this.inheritance;
    }

    public boolean isInheritance() {
        return this.inheritance;
    }
}