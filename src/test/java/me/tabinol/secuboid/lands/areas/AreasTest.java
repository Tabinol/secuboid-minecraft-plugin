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

import static me.tabinol.secuboid.lands.InitLands.WORLD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class AreasTest {

    @Test
    public void addAreaGetInside() {
        Areas areas = new Areas();
        Area area = new CuboidArea(true, WORLD, 0, 0, 0, 99, 255, 99);
        areas.add(area);
        for (int x = 0; x <= 99; x++) {
            for (int z = 0; z <= 99; z++) {
                List<Area> targetAreas = areas.get(x, 5, z, true);
                assertTrue(targetAreas.contains(area));
            }
        }
    }

    @Test
    public void deleteArea() {
        Areas areas = new Areas();
        Area area = new CuboidArea(true, WORLD, 0, 0, 0, 99, 255, 99);
        areas.add(area);
        areas.remove(area);
        for (int x = 0; x <= 99; x++) {
            for (int z = 0; z <= 99; z++) {
                List<Area> targetAreas = areas.get(x, 5, z, true);
                assertFalse(targetAreas.contains(area));
            }
        }
    }

    @Test
    public void addAreaGetOutside() {
        Areas areas = new Areas();
        Area area = new CuboidArea(true, WORLD, 0, 0, 0, 99, 255, 99);
        areas.add(area);
        List<Area> targetAreas = areas.get(-1, 5, -1, true);
        assertEquals(0, targetAreas.size());
        targetAreas = areas.get(100, 5, 99, true);
        assertEquals(0, targetAreas.size());
    }

    @Test
    public void addTwoAreas() {
        Areas areas = new Areas();
        Area area = new CuboidArea(true, WORLD, 0, 0, 0, 99, 255, 99);
        areas.add(area);
        Area area2 = new CuboidArea(true, WORLD, 98, 0, 98, 99, 255, 99);
        areas.add(area2);
        List<Area> targetAreas = areas.get(99, 5, 99, true);
        assertEquals(2, targetAreas.size());
    }
}
