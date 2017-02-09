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
package me.tabinol.secuboid.lands.collisions;

import me.tabinol.secuboid.FakePlayer;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.DefaultLand;
import me.tabinol.secuboid.lands.InitLands;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainerEverybody;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static me.tabinol.secuboid.lands.InitLands.WORLD;
import static org.junit.Assert.assertFalse;

/**
 * Test permissions and flags.
 * Created by Tabinol on 2017-02-08.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Secuboid.class)
public class LandPermissionsFlagsTest {

    private static final String LAND_PARENT = "parent";
    private static final String LAND_CHILD = "child";

    private static Secuboid secuboid;
    private static RealLand parent;
    private static RealLand child;
    private static DefaultLand defaultConfNoType;
    private static Player fakePlayer;

    @Before
    public void initLands() throws SecuboidLandException {
        InitLands initLands = new InitLands();
        Lands lands = initLands.getLands();
        secuboid = initLands.getSecuboid();
        defaultConfNoType = initLands.getDefaultConfNoType();

        // Create lands for test
        parent = lands.createLand(LAND_PARENT, new PlayerContainerNobody(),
                new CuboidArea(WORLD, 0, 0, 0, 99, 255, 99));
        child = lands.createLand(LAND_CHILD, new PlayerContainerNobody(),
                new CuboidArea(WORLD, 9, 9, 9, 60, 255, 60), parent);
        fakePlayer = new FakePlayer("fakeplayer");
    }

    @Test
    public void simplePermission() {
        parent.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(), false, true));
        assertFalse("Permission should be false",
                parent.getPermissionsFlags().checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
    }

    @Test
    public void simpleFlag() {
        parent.getPermissionsFlags().addFlag(
                secuboid.getPermissionsFlags().newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
        assertFalse("Flag should be false",
                parent.getPermissionsFlags().getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
    }

    @Test
    public void inheritPermission() {
        parent.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(), false, true));
        assertFalse("Permission should be false",
                child.getPermissionsFlags().checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
    }

    @Test
    public void inheritFlag() {
        parent.getPermissionsFlags().addFlag(
                secuboid.getPermissionsFlags().newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
        assertFalse("Flag should be false",
                child.getPermissionsFlags().getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
    }

    @Test
    public void defaultPermission() {

        defaultConfNoType.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(), false, true));
        assertFalse("Permission should be false",
                parent.getPermissionsFlags().checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
    }

    @Test
    public void defaultFlag() {
        defaultConfNoType.getPermissionsFlags().addFlag(
                secuboid.getPermissionsFlags().newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
        assertFalse("Flag should be false",
                parent.getPermissionsFlags().getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
    }
}
