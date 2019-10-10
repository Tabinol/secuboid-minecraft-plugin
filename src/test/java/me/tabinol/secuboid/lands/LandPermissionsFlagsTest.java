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

import me.tabinol.secuboid.bukkit.FakePlayer;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.*;
import me.tabinol.secuboid.Secuboid;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static me.tabinol.secuboid.lands.InitLands.WORLD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test permissions and flags. Created by Tabinol on 2017-02-08.
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
        private static WorldLand worldLand;
        private static Player fakePlayer;

        @Before
        public void initLands() throws SecuboidLandException {
                InitLands initLands = new InitLands();
                Lands lands = initLands.getLands();
                secuboid = initLands.getSecuboid();
                defaultConfNoType = initLands.getDefaultConfNoType();
                worldLand = initLands.getWorldLand();

                // Create lands for test
                parent = lands.createLand(LAND_PARENT, new PlayerContainerNobody(),
                                new CuboidArea(WORLD, 0, 0, 0, 99, 255, 99));
                child = lands.createLand(LAND_CHILD, new PlayerContainerNobody(),
                                new CuboidArea(WORLD, 9, 9, 9, 60, 255, 60), parent);
                fakePlayer = new FakePlayer(UUID.randomUUID(), "fakeplayer");
        }

        @Test
        public void simplePermission() {
                parent.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void simpleFlag() {
                parent.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
                assertFalse("Flag should be false", parent.getPermissionsFlags()
                                .getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
        }

        @Test
        public void inheritPermission() {
                parent.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", child.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void inheritFlag() {
                parent.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
                assertFalse("Flag should be false", child.getPermissionsFlags()
                                .getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
        }

        @Test
        public void defaultPermission() {

                defaultConfNoType.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void defaultFlag() {
                defaultConfNoType.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
                assertFalse("Flag should be false", parent.getPermissionsFlags()
                                .getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
        }

        @Test
        public void ownerPermission() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                parent.getPermissionsFlags().addPermission(new PlayerContainerOwner(), secuboid.getPermissionsFlags()
                                .newPermission(PermissionList.GOD.getPermissionType(), true, true));
                assertTrue("Permission should be true", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.GOD.getPermissionType()));
        }

        @Test
        public void ownerParentPermission() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                parent.getPermissionsFlags().addPermission(new PlayerContainerOwner(), secuboid.getPermissionsFlags()
                                .newPermission(PermissionList.USE.getPermissionType(), false, true));
                assertFalse("Permission should be false", child.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.USE.getPermissionType()));
        }

        @Test
        public void notOwnerPermission() {
                parent.getPermissionsFlags().addPermission(new PlayerContainerOwner(), secuboid.getPermissionsFlags()
                                .newPermission(PermissionList.GOD.getPermissionType(), true, true));
                assertFalse("Permission should be false", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.GOD.getPermissionType()));
        }

        @Test
        public void everybodyGlobal() {
                worldLand.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", worldLand.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void playerVsEverybody() {
                worldLand.getPermissionsFlags().addPermission(
                                new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                true, false));
                worldLand.getPermissionsFlags().addPermission(new PlayerContainerEverybody(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertTrue("Permission should be true", worldLand.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }
}
