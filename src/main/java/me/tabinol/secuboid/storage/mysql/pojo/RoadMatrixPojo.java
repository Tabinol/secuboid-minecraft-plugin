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

public final class RoadMatrixPojo {

    private final UUID landUUID;
    private final int areaId;
    private final int chunkX;
    private final int chunkZ;
    private final short[] matrix;

    public RoadMatrixPojo(final UUID landUUID, final int areaId, final int chunkX, final int chunkZ,
            final short[] matrix) {
        this.landUUID = landUUID;
        this.areaId = areaId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.matrix = matrix;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public int getAreaId() {
        return this.areaId;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public short[] getMatrix() {
        return this.matrix;
    }
}