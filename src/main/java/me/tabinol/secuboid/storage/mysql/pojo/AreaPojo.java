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

public final class AreaPojo {

    private final UUID landUUID;
    private final int areaId;
    private final boolean approved;
    private final String worldName;
    private final long areaTypeId;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public AreaPojo(final UUID landUUID, final int areaId, final boolean approved, final String worldName,
            final long areaTypeId, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.landUUID = landUUID;
        this.areaId = areaId;
        this.approved = approved;
        this.worldName = worldName;
        this.areaTypeId = areaTypeId;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public int getAreaId() {
        return this.areaId;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public boolean isApproved() {
        return this.approved;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public long getAreaTypeId() {
        return this.areaTypeId;
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getZ1() {
        return this.z1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public int getZ2() {
        return this.z2;
    }
}
