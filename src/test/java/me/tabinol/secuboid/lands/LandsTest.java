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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.lands.areas.Point;
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
    private static final String TEST_LINES = "testlines";

    private static PermissionsFlags permissionsFlags;
    private static Lands lands;

    /**
     *
     * @throws SecuboidLandException
     */
    @Before
    public void initLands() throws SecuboidLandException {

	// Prepare Mock
	PowerMockito.mockStatic(Secuboid.class);
	Secuboid secuboid = mock(Secuboid.class);

	// log
	Log log = mock(Log.class);
	doNothing().when(log).write(anyString());
	when(secuboid.getLog()).thenReturn(log);

	// Permissions Flags
	permissionsFlags = new PermissionsFlags(secuboid);
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
	List<Point> points = new ArrayList<Point>();
	points.add(new Point(150, 70, 150));
	points.add(new Point(150, 70, 200));
	points.add(new Point(200, 70, 250));
	points.add(new Point(250, 70, 250));
	lands.createLand(TEST_LINES, new PlayerContainerNobody(), new LinesArea(WORLD, 5, 5, 5, points));
    }

    /**
     *
     * @throws Exception
     */
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

    /**
     *
     * @throws Exception
     */
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

    /**
     *
     * @throws Exception
     */
    @Test
    public void verifyLines() throws Exception {

	RealLand land = lands.getLand(TEST_LINES);

	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	Date now = new Date();
	String strDate = sdfDate.format(now);
	System.out.println("x1: " + lands.getLand(TEST_LINES).getArea(1).getX1());
	System.out.println("y1: " + lands.getLand(TEST_LINES).getArea(1).getY1());
	System.out.println("z1: " + lands.getLand(TEST_LINES).getArea(1).getZ1());
	System.out.println("x2: " + lands.getLand(TEST_LINES).getArea(1).getX2());
	System.out.println("y2: " + lands.getLand(TEST_LINES).getArea(1).getY2());
	System.out.println("z2: " + lands.getLand(TEST_LINES).getArea(1).getZ2());
	System.out.println("startvolume : " + strDate);
	System.out.println(lands.getLand(TEST_LINES).getArea(1).getVolume());
	sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	now = new Date();
	strDate = sdfDate.format(now);
	System.out.println("endvolume : " + strDate);

	// Inside point check
	if (!land.isLocationInside(WORLD, 150, 70, 150)) {
	    throw new Exception("Location error");
	}
	if (!land.isLocationInside(WORLD, 151, 69, 151)) {
	    throw new Exception("Location error");
	}
	// Outside point check
	if (land.isLocationInside(WORLD, 149, 71, 249)) {
	    throw new Exception("Location error");
	}
    }
}
