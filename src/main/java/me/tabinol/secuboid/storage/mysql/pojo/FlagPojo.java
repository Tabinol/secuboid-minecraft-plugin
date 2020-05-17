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

public final class FlagPojo {

    private final long id;
    private final UUID landUUID;
    private final long flagId;
    private final boolean inheritance;

    public FlagPojo(final long id, final UUID landUUID, final long flagId, final boolean inheritance) {
        this.id = id;
        this.landUUID = landUUID;
        this.flagId = flagId;
        this.inheritance = inheritance;
    }

    public long getId() {
        return this.id;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public long getFlagId() {
        return this.flagId;
    }

    public boolean getInheritance() {
        return this.inheritance;
    }

    public boolean isInheritance() {
        return this.inheritance;
    }
}