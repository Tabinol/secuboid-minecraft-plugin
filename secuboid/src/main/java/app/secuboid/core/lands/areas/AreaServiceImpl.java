/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.secuboid.core.lands.areas;

import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.lands.areas.AreaService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import static app.secuboid.core.messages.Log.log;

@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final Map<String, Map<Long, Set<Area>>> worldNameToRegionXZToAreas = new HashMap<>();

    public void add(World world, Area area) {
        String worldName = world.getName();
        performInRegions(area, (rx, rz) -> addInRegion(worldName, rx, rz, area));
    }

    public void remove(World world, Area area) {
        String worldName = world.getName();
        performInRegions(area, (rx, rz) -> removeInRegion(worldName, rx, rz, area));
    }

    @Override
    public Set<Area> getAreas(World world, int x, int z) {
        String worldName = world.getName();

        return getAreas(worldName, x, 0, z, false);
    }

    @Override
    public Set<Area> getAreas(World world, int x, int y, int z) {
        String worldName = world.getName();

        return getAreas(worldName, x, y, z, true);
    }

    @Override
    public Set<Area> getAreas(Location loc) {
        String worldName = loc.getWorld().getName();

        return getAreas(worldName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
    }

    @Override
    public Area getArea(Location loc) {
        String worldName = loc.getWorld().getName();
        Set<Area> areas = getAreas(worldName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);

        // TODO Area priority/child system
        return areas.stream().findAny().orElse(null);
    }

    private Set<Area> getAreas(String worldName, int x, int y, int z, boolean isY) {
        Map<Long, Set<Area>> regionXZToAreas = worldNameToRegionXZToAreas.get(worldName);

        if (regionXZToAreas == null) {
            log().log(Level.WARNING, "The world name {} does not exist yet", worldName);
            return Collections.emptySet();
        }

        long regionXZ = getRegionXZFromLocation(x, z);
        Set<Area> areas = regionXZToAreas.get(regionXZ);

        if (areas == null) {
            return Collections.emptySet();
        }

        Set<Area> result = new HashSet<>();
        for (Area area : areas) {
            if (isY ? area.isLocationInside(x, y, z) : area.isLocationInsideSquare(x, z)) {
                result.add(area);
            }
        }
        return result;
    }

    private void performInRegions(Area area, BiConsumer<Integer, Integer> bc) {
        int regionX1 = area.getX1() >> 9;
        int regionX2 = area.getX2() >> 9;
        int regionZ1 = area.getZ1() >> 9;
        int regionZ2 = area.getZ2() >> 9;

        for (int regionX = regionX1; regionX <= regionX2; regionX++) {
            for (int regionZ = regionZ1; regionZ <= regionZ2; regionZ++) {
                bc.accept(regionX, regionZ);
            }
        }
    }

    private void addInRegion(String worldName, int regionX, int regionZ, Area area) {
        Map<Long, Set<Area>> regionXZToAreas = worldNameToRegionXZToAreas.computeIfAbsent(worldName, k -> new HashMap<>());
        long regionXZ = getRegionXZFromRegion(regionX, regionZ);
        regionXZToAreas.computeIfAbsent(regionXZ, k -> new HashSet<>()).add(area);
    }

    private void removeInRegion(String worldName, int regionX, int regionZ, Area area) {
        Map<Long, Set<Area>> regionXZToAreas = worldNameToRegionXZToAreas.get(worldName);

        if (regionXZToAreas == null) {
            return;
        }

        long regionXZ = getRegionXZFromRegion(regionX, regionZ);
        Set<Area> areas = regionXZToAreas.get(regionXZ);

        if (areas == null) {
            return;
        }

        areas.remove(area);

        if (areas.isEmpty()) {
            regionXZToAreas.remove(regionXZ);
        }
    }

    private long getRegionXZFromLocation(int x, int z) {
        int regionX = x >> 9;
        int regionZ = z >> 9;
        return getRegionXZFromRegion(regionX, regionZ);
    }

    private long getRegionXZFromRegion(int regionX, int regionZ) {
        return (((long) regionX) << 32) | (regionZ & 0xFFFFFFFFL);
    }
}
