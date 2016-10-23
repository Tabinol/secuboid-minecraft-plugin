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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.parameters.Parameters;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
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

    private static Parameters parameters;
    private static Lands lands;

    /**
     *
     * @throws SecuboidLandException
     */
    @Before
    public void initLands() throws SecuboidLandException {

        // Prepare Mock
        PowerMockito.mockStatic(Secuboid.class);
        Mockito.when(Secuboid.getThisPlugin()).thenReturn(null);
        Mockito.when(Secuboid.getConfigFolder()).thenReturn(new File("src/main/resources/"));
        parameters = new Parameters();
        Mockito.when(Secuboid.getStaticParameters()).thenReturn(parameters);
        Types types = new Types();
        Mockito.when(Secuboid.getStaticTypes()).thenReturn(types);

        // Create lands for test
        lands = new Lands();
        lands.createLand(TEST_CUBOID, new PlayerContainerNobody(), new CuboidArea(WORLD, 0, 0, 0, 99, 255, 99));
        lands.createLand(TEST_CYLINDER, new PlayerContainerNobody(), new CylinderArea(WORLD, 9, 9, 9, 120, 255, 100));
        List<LineLine> lines = new ArrayList<LineLine>();
        lines.add(new LineLine(150, 70, 150, 150, 70, 200, 5, 5, 5, 5));
        lines.add(new LineLine(150, 70, 200, 200, 70, 250, 5, 5, 5, 5));
        lines.add(new LineLine(200, 70, 250, 250, 70, 250, 5, 5, 5, 5));
        lands.createLand(TEST_LINES, new PlayerContainerNobody(), new LinesArea(WORLD, lines));
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void verifyCuboid() throws Exception {

        Land land = lands.getLand(TEST_CUBOID);

        // Volume check
        if(land.getArea(1).getVolume() != 100 * 256 * 100) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if(!land.isLocationInside(WORLD, 1, 1, 1)) {
            throw new Exception("Location error");
        }

        // Outside point check
        if(land.isLocationInside(WORLD, 100, 1, 100)) {
            throw new Exception("Location error");
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void verifyCylinder() throws Exception {

        Land land = lands.getLand(TEST_CYLINDER);

        // Volume check
        if(land.getArea(1).getVolume() != (long) (Math.PI * 56 * 46 * 247)) {
            throw new Exception("Volume error");
        }

        // Inside point check
        if(!land.isLocationInside(WORLD, 65, 30, 100)) {
            throw new Exception("Location error");
        }

        // Just a little bit outside
        if(land.isLocationInside(WORLD, 65, 30, 101)) {
            throw new Exception("Location error");
        }

        // Outside point check
        if(land.isLocationInside(WORLD, 120, 40, 100)) {
            throw new Exception("Location error");
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void verifyLines() throws Exception {

        Land land = lands.getLand(TEST_LINES);

        System.out.println(lands.getLand(TEST_LINES).getArea(1).getVolume());

        // Inside point check
        // TODO Activate tests
        //if(!land.isLocationInside(WORLD, 151, 71, 1225)) {
        //    throw new Exception("Location error");
        //}

        // Outside point check
        //if(land.isLocationInside(WORLD, 149, 71, 249)) {
        //    throw new Exception("Location error");
        //}
    }
}
