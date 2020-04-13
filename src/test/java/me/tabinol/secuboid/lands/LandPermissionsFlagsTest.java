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
package me.tabinol.secuboid.lands;

import static me.tabinol.secuboid.lands.InitLands.WORLD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.bukkit.FakePlayer;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainerEverybody;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;

/**
 * Test permissions and flags. Created by Tabinol on 2017-02-08.
 */
public final class LandPermissionsFlagsTest {

        private static final String LAND_PARENT = "parent";
        private static final String LAND_CHILD = "child";

        private Secuboid secuboid;
        private Lands lands;
        private Land parent;
        private Land child;
        private Player fakePlayer;

        @Before
        public void initLands() throws SecuboidLandException {
                final InitLands initLands = new InitLands();
                lands = initLands.getLands();
                secuboid = initLands.getSecuboid();

                // Create lands for test
                parent = lands.createLand(LAND_PARENT, PlayerContainerNobody.getInstance(),
                                new CuboidArea(true, WORLD, 0, 0, 0, 99, 255, 99));
                child = lands.createLand(LAND_CHILD, PlayerContainerNobody.getInstance(),
                                new CuboidArea(true, WORLD, 9, 9, 9, 60, 255, 60), parent);
                fakePlayer = new FakePlayer(UUID.randomUUID(), "fakeplayer").getPlayer();
        }

        @Test
        public void simplePermission() {
                parent.getPermissionsFlags().addPermission(PlayerContainerEverybody.getInstance(),
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
                parent.getPermissionsFlags().addPermission(PlayerContainerEverybody.getInstance(),
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

                lands.getDefaultConf(null).addPermission(PlayerContainerEverybody.getInstance(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void defaultFlag() {
                lands.getDefaultConf(null).addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.ANIMAL_SPAWN.getFlagType(), false, true));
                assertFalse("Flag should be false", parent.getPermissionsFlags()
                                .getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean());
        }

        @Test
        public void ownerPermission() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                parent.getPermissionsFlags().addPermission(PlayerContainerOwner.getInstance(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.GOD.getPermissionType(),
                                                true, true));
                assertTrue("Permission should be true", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.GOD.getPermissionType()));
        }

        @Test
        public void ownerParentPermission() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                parent.getPermissionsFlags().addPermission(PlayerContainerOwner.getInstance(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.USE.getPermissionType(),
                                                false, true));
                assertFalse("Permission should be false", child.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.USE.getPermissionType()));
        }

        @Test
        public void notOwnerPermission() {
                parent.getPermissionsFlags().addPermission(PlayerContainerOwner.getInstance(),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.GOD.getPermissionType(),
                                                true, true));
                assertFalse("Permission should be false", parent.getPermissionsFlags()
                                .checkPermissionAndInherit(fakePlayer, PermissionList.GOD.getPermissionType()));
        }

        @Test
        public void everybodyGlobal() {
                lands.getOutsideLandPermissionsFlags((String) null)
                                .addPermission(PlayerContainerEverybody.getInstance(), secuboid.getPermissionsFlags()
                                                .newPermission(PermissionList.BUILD.getPermissionType(), false, true));
                assertFalse("Permission should be false", lands.getOutsideLandPermissionsFlags((String) null)
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void playerVsEverybody() {
                lands.getOutsideLandPermissionsFlags((String) null).addPermission(
                                new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()),
                                secuboid.getPermissionsFlags().newPermission(PermissionList.BUILD.getPermissionType(),
                                                true, false));
                lands.getOutsideLandPermissionsFlags((String) null)
                                .addPermission(PlayerContainerEverybody.getInstance(), secuboid.getPermissionsFlags()
                                                .newPermission(PermissionList.BUILD.getPermissionType(), false, true));
                assertTrue("Permission should be true", lands.getOutsideLandPermissionsFlags((String) null)
                                .checkPermissionAndInherit(fakePlayer, PermissionList.BUILD.getPermissionType()));
        }

        @Test
        public void ownerInheritance() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                assertTrue("Player must be owner", child.isOwner(fakePlayer));
        }

        @Test
        public void ownerPermissionInheritance() {
                parent.getPermissionsFlags()
                                .addPermission(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()),
                                                secuboid.getPermissionsFlags().newPermission(
                                                                PermissionList.LAND_OWNER.getPermissionType(), true,
                                                                true));
                assertTrue("Player must be owner", child.isOwner(fakePlayer));
        }

        @Test
        public void ownerNoInheritance() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                child.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.INHERIT_OWNER.getFlagType(), false, true));
                assertFalse("Player must not be owner", child.isOwner(fakePlayer));
        }

        @Test
        public void tenantInheritance() {
                parent.setRented(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                assertTrue("Player must be tenant", child.isTenant(fakePlayer));
        }

        @Test
        public void tenantPermissionInheritance() {
                parent.setRented(new PlayerContainerPlayer(secuboid, UUID.randomUUID()));
                parent.getPermissionsFlags()
                                .addPermission(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()),
                                                secuboid.getPermissionsFlags().newPermission(
                                                                PermissionList.LAND_TENANT.getPermissionType(), true,
                                                                true));
                assertTrue("Player must be tenant", child.isTenant(fakePlayer));
        }

        @Test
        public void tennatNoInheritance() {
                parent.setRented(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                child.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.INHERIT_TENANT.getFlagType(), false, true));
                assertFalse("Player must not be tenant", child.isTenant(fakePlayer));
        }

        @Test
        public void residentInheritance() {
                parent.addResident(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                assertTrue("Player must be resident", child.isResident(fakePlayer));
        }

        @Test
        public void residentNoInheritance() {
                parent.setOwner(new PlayerContainerPlayer(secuboid, fakePlayer.getUniqueId()));
                child.getPermissionsFlags().addFlag(secuboid.getPermissionsFlags()
                                .newFlag(FlagList.INHERIT_RESIDENTS.getFlagType(), false, true));
                assertFalse("Player must not be resident", child.isResident(fakePlayer));
        }
}
