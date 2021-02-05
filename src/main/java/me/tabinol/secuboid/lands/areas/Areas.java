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
package me.tabinol.secuboid.lands.areas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the area database.
 */
public final class Areas {

    private final Map<Long, List<Area>[]> regionXZToChunkXZAreas;

    public Areas() {
        regionXZToChunkXZAreas = new HashMap<>();
    }

    public List<Area> get(int x, int y, int z, boolean isY) {
        long regionXZ = getRegionXZ(x, z);
        List<Area>[] chunkXZAreas = regionXZToChunkXZAreas.get(regionXZ);
        if (chunkXZAreas == null) {
            return Collections.emptyList();
        }

        int chunkInRegionXZ = getChunkInRegionXZ(x, z);
        List<Area> chunkAreas = chunkXZAreas[chunkInRegionXZ];
        if (chunkAreas == null) {
            return Collections.emptyList();
        }

        List<Area> areas = new ArrayList<>();

        for (Area area : chunkAreas) {

            if (isY ? area.isLocationInside(x, y, z) : area.isLocationInsideSquare(x, z)) {
                areas.add(area);
            }
        }
        return areas;
    }

    public void add(Area area) {
        int chunkX1 = area.getX1() >> 4;
        int chunkX2 = area.getX2() >> 4;
        int chunkZ1 = area.getZ1() >> 4;
        int chunkZ2 = area.getZ2() >> 4;

        for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
            for (int chunkZ = chunkZ1; chunkZ <= chunkZ2; chunkZ++) {
                addInChunk(chunkX, chunkZ, area);
            }
        }
    }

    public void remove(Area area) {
        int chunkX1 = area.getX1() >> 4;
        int chunkX2 = area.getX2() >> 4;
        int chunkZ1 = area.getZ1() >> 4;
        int chunkZ2 = area.getZ2() >> 4;

        for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
            for (int chunkZ = chunkZ1; chunkZ <= chunkZ2; chunkZ++) {
                removeInChunk(chunkX, chunkZ, area);
            }
        }
    }

    private void addInChunk(int chunkX, int chunkZ, Area area) {
        long regionXZ = getRegionXZFromChunk(chunkX, chunkZ);

        @SuppressWarnings("unchecked")
        List<Area>[] chunkXZAreas = regionXZToChunkXZAreas.computeIfAbsent(regionXZ, k -> new List[1024]);

        int chunkInRegionXZ = getChunkInRegionXZFromChunk(chunkX, chunkZ);
        List<Area> areas = chunkXZAreas[chunkInRegionXZ];
        if (areas == null) {
            areas = new ArrayList<Area>();
            chunkXZAreas[chunkInRegionXZ] = areas;
        }
        areas.add(area);
    }

    private void removeInChunk(int chunkX, int chunkZ, Area area) {
        long regionXZ = getRegionXZFromChunk(chunkX, chunkZ);

        List<Area>[] chunkXZAreas = regionXZToChunkXZAreas.get(regionXZ);
        if (chunkXZAreas == null) {
            return;
        }

        int chunkInRegionXZ = getChunkInRegionXZFromChunk(chunkX, chunkZ);
        List<Area> areas = chunkXZAreas[chunkInRegionXZ];
        if (areas != null) {
            areas.remove(area);
        }
        if (areas.isEmpty()) {
            chunkXZAreas[chunkInRegionXZ] = null;
        }

        if (isArrayAllNull(chunkXZAreas)) {
            regionXZToChunkXZAreas.remove(regionXZ);
        }
    }

    private long getRegionXZ(int x, int z) {
        int regionX = x >> 9;
        int regionZ = z >> 9;
        return (((long) regionX) << 32) | (regionZ & 0xFFFFFFFFL);
    }

    private int getChunkInRegionXZ(int x, int z) {
        int chunkInRegionX = x >> 4 & 0x1F;
        int chunkInRegionZ = z >> 4 & 0x1F;
        return (chunkInRegionX << 5) | chunkInRegionZ;
    }

    private long getRegionXZFromChunk(int chunkX, int chunkZ) {
        int regionX = chunkX >> 5;
        int regionZ = chunkZ >> 5;
        return (((long) regionX) << 32) | (regionZ & 0xFFFFFFFFL);
    }

    private int getChunkInRegionXZFromChunk(int chunkX, int chunkZ) {
        int chunkInRegionX = chunkX & 0x1F;
        int chunkInRegionZ = chunkZ & 0x1F;
        return (chunkInRegionX << 5) | chunkInRegionZ;
    }

    private boolean isArrayAllNull(List<Area>[] chunkXZAreas) {
        for (int i = 0; i < chunkXZAreas.length; i++) {
            if (chunkXZAreas[i] != null) {
                return false;
            }
        }
        return true;
    }
}
