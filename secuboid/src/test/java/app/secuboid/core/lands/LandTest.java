/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
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
package app.secuboid.core.lands;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LandType;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.core.lands.areas.AreaCuboidImpl;
import app.secuboid.core.persistence.jpa.AreaJPA;
import app.secuboid.core.persistence.jpa.LandJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static app.secuboid.api.persistence.WithId.NON_EXISTING_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LandTest {

    private static final int ID_AREA_1 = 1;
    private static final int ID_AREA_2 = 2;

    private Land worldLand;
    private Land areaLand;

    @BeforeEach
    void beforeEach() {
        worldLand = LandImpl.builder()
                .jPA(LandJPA.builder()
                        .id(1L)
                        .type(LandType.WORLD)
                        .name("world001")
                        .build())
                .build();
        areaLand = LandImpl.builder()
                .jPA(LandJPA.builder()
                        .id(NON_EXISTING_ID)
                        .type(LandType.LAND)
                        .name("test001")
                        .build())
                .parent(worldLand)
                .build();
    }

    @Test
    void when_get_path_world_then_return_slash_world_name() {
        assertEquals("/world001", worldLand.getPathName());
    }

    @Test
    void when_get_path_land_then_return_slash_world_name_slash_land_name() {
        assertEquals("/world001/test001", areaLand.getPathName());
    }

    @Test
    void when_get_path_area_then_return_slash_world_name_slash_land_name_area_id() {
        Area area = new AreaCuboidImpl(AreaJPA.builder()
                .id(ID_AREA_1)
                .x1(0)
                .y1(0)
                .z1(0)
                .x2(99)
                .y2(255)
                .z2(99)
                .build(), areaLand);
        worldLand.getAreas().add(area);

        assertEquals("/world001/test001:1", area.getPathName());
    }

//    @Test
//    void when_inside_area_then_get_it() {
//        Area area = AreaCuboidImpl.builder()
//                .id(ID_AREA_1)
//                .x1(0)
//                .y1(0)
//                .z1(0)
//                .x2(99)
//                .y2(255)
//                .z2(99)
//                .land(areaLand)
//                .build();
//        worldLand.getAreas().add(area);
//
//        for (int x = 0; x <= 99; x++) {
//            for (int z = 0; z <= 99; z++) {
//                Set<Area> targetAreas = worldLand.get(x, 5, z);
//                assertTrue(targetAreas.contains(area));
//            }
//        }
//    }
//
//    @Test
//    void when_area_removed_then_not_in_any_set() {
//        Area area = AreaCuboidImpl.builder()
//                .id(ID_AREA_1)
//                .x1(0)
//                .y1(0)
//                .z1(0)
//                .x2(99)
//                .y2(255)
//                .z2(99)
//                .land(areaLand)
//                .build();
//        worldLand.getAreas().add(area);
//        worldLand.getAreas().remove(area);
//
//        for (int x = 0; x <= 99; x++) {
//            for (int z = 0; z <= 99; z++) {
//                Set<Area> targetAreas = worldLand.get(x, 5, z);
//                assertFalse(targetAreas.contains(area));
//            }
//        }
//    }
//
//    @Test
//    void when_outside_area_then_not_get_it() {
//        Area area = new AreaImpl(ID_AREA_1, new CuboidAreaFormImpl(0, 0, 0, 99, 255, 99), areaLand);
//        ((WorldLandImpl) worldLand).add(area);
//
//        Set<Area> targetAreas = worldLand.get(-1, 5, -1);
//        assertEquals(0, targetAreas.size());
//        targetAreas = worldLand.get(100, 5, 99);
//        assertEquals(0, targetAreas.size());
//    }
//
//    @Test
//    void when_add_two_areas_then_get_both() {
//        Area area = new AreaImpl(ID_AREA_1, new CuboidAreaFormImpl(0, 0, 0, 99, 255, 99), areaLand);
//        ((WorldLandImpl) worldLand).add(area);
//        Area area2 = new AreaImpl(ID_AREA_2, new CuboidAreaFormImpl(98, 0, 98, 99, 255, 99), areaLand);
//        ((WorldLandImpl) worldLand).add(area2);
//
//        Set<Area> targetAreas = worldLand.get(99, 5, 99);
//        assertEquals(2, targetAreas.size());
//    }
//
//    @Test
//    void when_land_is_descendants_then_return_true() {
//        ((LandImpl) worldLand).setChild(areaLand);
//
//        assertTrue(areaLand.isDescendantsOf(worldLand));
//    }
//
//    @Test
//    void when_land_is_not_descendants_then_return_false() {
//        assertFalse(worldLand.isDescendantsOf(areaLand));
//    }
}
