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

import java.util.Optional;
import java.util.UUID;

public final class PlayerContainerPojo {

    private final int id;
    private final int playerContainerTypeId;
    private final Optional<UUID> playerUUIDOpt;
    private final Optional<String> parameterOpt;

    public PlayerContainerPojo(final int id, final int playerContainerTypeId, final Optional<UUID> playerUUIDOpt,
            final Optional<String> parameterOpt) {
        this.id = id;
        this.playerContainerTypeId = playerContainerTypeId;
        this.playerUUIDOpt = playerUUIDOpt;
        this.parameterOpt = parameterOpt;
    }

    public int getId() {
        return this.id;
    }

    public int getPlayerContainerTypeId() {
        return this.playerContainerTypeId;
    }

    public Optional<UUID> getPlayerUUIDOpt() {
        return this.playerUUIDOpt;
    }

    public Optional<String> getParameterOpt() {
        return this.parameterOpt;
    }
}