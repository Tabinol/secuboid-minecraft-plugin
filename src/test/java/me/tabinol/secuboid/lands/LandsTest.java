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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RegionMatrix;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Log;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import org.powermock.api.mockito.PowerMockito;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests for lands.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Secuboid.class)
public class LandsTest {

    private static final String WORLD = "world";
    private static final String TEST_CUBOID = "testcuboid";
    private static final String TEST_CYLINDER = "testcylinder";
    private static final String TEST_ROAD = "road";

    private static Lands lands;

    @Before
    public void initLands() throws SecuboidLandException {

        // Prepare Mock
        PowerMockito.mockStatic(Secuboid.class);
        Secuboid secuboid = mock(Secuboid.class);

        // log
        Log log = mock(Log.class);
        doNothing().when(log).info(anyString());
        doNothing().when(log).warning(anyString());
        doNothing().when(log).severe(anyString());
        doNothing().when(log).debug(anyString());
        when(secuboid.getLog()).thenReturn(log);

        // Permissions Flags
        PermissionsFlags permissionsFlags = new PermissionsFlags(secuboid);
        when(secuboid.getPermissionsFlags()).thenReturn(permissionsFlags);

        // Tyles
        Types types = new Types();
        when(secuboid.getTypes()).thenReturn(types);

        // Server
        PluginManager pm = mock(PluginManager.class);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(pm);
        when(secuboid.getServer()).thenReturn(server);

        // Lands
        lands = new Lands(secuboid);
        when(secuboid.getLands()).thenReturn(lands);

        StorageThread storageThread = mock(StorageThread.class);
        doNothing().when(storageThread).saveLand(any(RealLand.class));
        when(secuboid.getStorageThread()).thenReturn(storageThread);

        // Create lands for test
        lands.createLand(TEST_CUBOID, new PlayerContainerNobody(), new CuboidArea(WORLD, 0, 0, 0, 99, 255, 99));
        lands.createLand(TEST_CYLINDER, new PlayerContainerNobody(), new CylinderArea(WORLD, 9, 9, 9, 120, 255, 100));
        RegionMatrix regionMatrix = new RegionMatrix();
        regionMatrix.addPoint(200, 200);
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
        if (land.getArea(1).getVolume() != (long) (Math.PI * 56 * 46 * 247)) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if (!land.isLocationInside(WORLD, 65, 30, 100)) {
            throw new Exception("Location error");
        }

        // Just a little bit outside
        if (land.isLocationInside(WORLD, 65, 30, 101)) {
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

        // Inside point check
        if (!land.isLocationInside(WORLD, 200, 30, 200)) {
            throw new Exception("Location error");
        }

        // Just a little bit outside
        if (land.isLocationInside(WORLD, 201, 30, 200)) {
            throw new Exception("Location error");
        }
    }
}
