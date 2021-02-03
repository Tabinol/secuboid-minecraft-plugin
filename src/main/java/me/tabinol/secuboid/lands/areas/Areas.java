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
        addRemove(area, true);
    }

    public void remove(Area area) {
        addRemove(area, false);
    }

    private void addRemove(Area area, boolean isAdd) {
        int x1 = area.getX1();
        int z1 = area.getZ1();
        int x2 = area.getX2();
        int z2 = area.getZ2();
        int minRegionX = x1 >> 9;
        int maxRegionX = x2 >> 9;
        int minRegionZ = z1 >> 9;
        int maxRegionZ = z2 >> 9;
        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                if (isAdd) {
                    addInRegion(x1, x2, z1, z2, regionX, regionZ, area);
                } else {
                    removeInRegion(x1, x2, z1, z2, regionX, regionZ, area);
                }
            }
        }
    }

    private void addInRegion(int x1, int x2, int z1, int z2, int regionX, int regionZ, Area area) {
        long regionXZ = getRegionXZ(regionX, regionZ);

        @SuppressWarnings("unchecked")
        List<Area>[] chunkXZAreas = regionXZToChunkXZAreas.computeIfAbsent(regionXZ, k -> new List[1024]);

        int minChunkX = x1 <= regionX << 9 ? 0 : x1 >> 4 & 0x1F;
        int maxChunkX = x2 > ((regionX << 9) | 0x1F) ? 31 : x2 >> 4 & 0x1F;
        int minChunkZ = z1 <= regionZ << 9 ? 0 : z1 & 0x1F;
        int maxChunkZ = z2 > ((regionZ << 9) | 0x1F) ? 31 : z2 >> 4 & 0x1F;
        for (int iChunkX = minChunkX; iChunkX <= maxChunkX; iChunkX++) {
            for (int iChunkZ = minChunkZ; iChunkZ <= maxChunkZ; iChunkZ++) {
                int chunkInRegionXZ = getChunkInRegionXZ(iChunkX, iChunkZ);
                List<Area> areas = chunkXZAreas[chunkInRegionXZ];
                if (areas == null) {
                    areas = new ArrayList<Area>();
                    chunkXZAreas[chunkInRegionXZ] = areas;
                }
                areas.add(area);
            }
        }
    }

    private void removeInRegion(int x1, int x2, int z1, int z2, int regionX, int regionZ, Area area) {
        long regionXZ = getRegionXZ(regionX, regionZ);
        List<Area>[] chunkXZAreas = regionXZToChunkXZAreas.get(regionXZ);
        if (chunkXZAreas == null) {
            return;
        }

        int minChunkX = x1 <= regionX << 9 ? 0 : x1 >> 4 & 0x1F;
        int maxChunkX = x2 > ((regionX << 9) | 0x1F) ? 31 : x2 >> 4 & 0x1F;
        int minChunkZ = z1 <= regionZ << 9 ? 0 : z1 & 0x1F;
        int maxChunkZ = z2 > ((regionZ << 9) | 0x1F) ? 31 : z2 >> 4 & 0x1F;
        for (int iChunkX = minChunkX; iChunkX <= maxChunkX; iChunkX++) {
            for (int iChunkZ = minChunkZ; iChunkZ <= maxChunkZ; iChunkZ++) {
                int chunkInRegionXZ = getChunkInRegionXZ(iChunkX, iChunkZ);
                List<Area> areas = chunkXZAreas[chunkInRegionXZ];
                if (areas != null) {
                    areas.remove(area);
                }
                if (areas.isEmpty()) {
                    chunkXZAreas[chunkInRegionXZ] = null;
                }
            }
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
        final int chunkX = x >> 4 & 0x1F;
        final int chunkZ = z >> 4 & 0x1F;
        return (chunkX << 5) | (chunkZ & 0x1F);
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
