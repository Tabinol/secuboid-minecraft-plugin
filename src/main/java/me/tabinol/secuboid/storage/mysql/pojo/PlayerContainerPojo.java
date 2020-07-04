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

public final class PlayerContainerPojo {

    private final long id;
    private final long playerContainerTypeId;
    private final UUID playerUUIDNullable;
    private final String parameterNullable;

    public PlayerContainerPojo(final long id, final long playerContainerTypeId, final UUID playerUUIDNullable,
                               final String parameterNullable) {
        this.id = id;
        this.playerContainerTypeId = playerContainerTypeId;
        this.playerUUIDNullable = playerUUIDNullable;
        this.parameterNullable = parameterNullable;
    }

    public long getId() {
        return this.id;
    }

    public long getPlayerContainerTypeId() {
        return this.playerContainerTypeId;
    }

    public UUID getPlayerUUIDNullable() {
        return this.playerUUIDNullable;
    }

    public String getParameterNullable() {
        return this.parameterNullable;
    }
}