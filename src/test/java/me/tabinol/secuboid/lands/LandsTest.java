/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

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
package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RegionMatrix;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import org.junit.Before;
import org.junit.Test;

import static me.tabinol.secuboid.lands.InitLands.WORLD;

/**
 * Tests for lands.
 */
public class LandsTest {

    private static final String TEST_CUBOID = "testcuboid";
    private static final String TEST_CYLINDER = "testcylinder";
    private static final String TEST_ROAD = "road";

    private static Lands lands;

    @Before
    public void initLands() throws SecuboidLandException {
        lands = new InitLands().getLands();

        // Create lands for test
        lands.createLand(TEST_CUBOID, new PlayerContainerNobody(), new CuboidArea(WORLD, 0, 0, 0, 99, 255, 99));
        lands.createLand(TEST_CYLINDER, new PlayerContainerNobody(), new CylinderArea(WORLD, 9, 9, 9, 120, 255, 100));
        RegionMatrix regionMatrix = new RegionMatrix();
        regionMatrix.addPoint(200, 200);
        regionMatrix.addPoint(-200, -200);
        lands.createLand(TEST_ROAD, new PlayerContainerNobody(), new RoadArea(WORLD, 0, 255, regionMatrix));
    }

    @Test
    public void verifyCuboid() throws Exception {

        RealLand land = lands.getLand(TEST_CUBOID);

        // Volume check
        if (land.getArea(1).getVolume() != 100 * 256 * 100) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if (!land.isLocationInside(WORLD, 1, 1, 1)) {
            throw new Exception("Location error");
        }

        // Outside point check
        if (land.isLocationInside(WORLD, 100, 1, 100)) {
            throw new Exception("Location error");
        }
    }

    @Test
    public void verifyCylinder() throws Exception {

        RealLand land = lands.getLand(TEST_CYLINDER);

        // Volume check
        if (land.getArea(1).getVolume() != Math.round(Math.PI * 55.5 * 45.5 * 247)) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if (!land.isLocationInside(WORLD, 10, 30, 55)) {
            throw new Exception("Location error");
        }

        // Just a little bit outside
        if (land.isLocationInside(WORLD, 64, 30, 102)) {
            throw new Exception("Location error");
        }

        // Outside point check
        if (land.isLocationInside(WORLD, 120, 40, 100)) {
            throw new Exception("Location error");
        }
    }

    @Test
    public void verifyRoad() throws Exception {

        RealLand land = lands.getLand(TEST_ROAD);

        // Volume check
        if (land.getArea(1).getVolume() != 256 * 2) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if (!land.isLocationInside(WORLD, 200, 30, 200)) {
            throw new Exception("Location error");
        }

        // Inside negative point check
        if (!land.isLocationInside(WORLD, -200, 30, -200)) {
            throw new Exception("Location error");
        }

        // Just a little bit outside
        if (land.isLocationInside(WORLD, 201, 30, 200)) {
            throw new Exception("Location error");
        }
    }
}
