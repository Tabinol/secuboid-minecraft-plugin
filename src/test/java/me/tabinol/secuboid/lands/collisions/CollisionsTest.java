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

import static me.tabinol.secuboid.lands.InitLands.WORLD;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.InitLands;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;

/**
 * Test land collisions.
 */
public final class CollisionsTest {

    private Secuboid secuboid;
    private Lands lands;

    @Before
    public void initCollisions() throws SecuboidLandException {
        final InitLands initLands = new InitLands();
        secuboid = initLands.getSecuboid();
        lands = initLands.getLands();
        lands.createLand("land1", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 0, 0, 0, 100, 255, 100));
    }

    private boolean isError(final Collisions collisions, final Collisions.LandError landError) {
        for (final CollisionsEntry collisionEntry : collisions.getCollisions()) {
            if (collisionEntry.getError() == landError) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void landCollision() throws SecuboidLandException {
        final Collisions collisions = new Collisions(secuboid, WORLD, "landT", null, Collisions.LandAction.LAND_ADD, 0,
                new CuboidArea(true, WORLD, 10, 0, 10, 120, 255, 120), null, PlayerContainerNobody.getInstance(), true,
                false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.COLLISION)) {
            fail("Land collision not detected");
        }
    }

    @Test
    public void landOutsideParent() throws SecuboidLandException {
        final Collisions collisions = new Collisions(secuboid, WORLD, "landT", null, Collisions.LandAction.LAND_ADD, 0,
                new CuboidArea(true, WORLD, 10, 0, 10, 120, 255, 120), lands.getLand("land1"),
                PlayerContainerNobody.getInstance(), true, false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.OUT_OF_PARENT)) {
            fail("Land outside parent not detected");
        }
    }

    @Test
    public void landChildrenOutside() throws SecuboidLandException {
        final Land land2 = lands.createLand("land2", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 1000, 0, 1000, 1100, 255, 1100));
        lands.createLand("land3", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 1000, 0, 1000, 1100, 255, 1100), land2);
        final Collisions collisions = new Collisions(secuboid, WORLD, "land2", land2, Collisions.LandAction.AREA_MODIFY,
                1, new CuboidArea(true, WORLD, 10, 0, 10, 120, 255, 120), null, PlayerContainerNobody.getInstance(),
                true, false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.CHILD_OUT_OF_BORDER)) {
            fail("Land has children outside not detected");
        }
    }

    @Test
    public void landHasChild() throws SecuboidLandException {
        final Land land4 = lands.createLand("land4", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 2000, 0, 2000, 2100, 255, 2100));
        lands.createLand("land5", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 2000, 0, 2000, 2100, 255, 2100), land4);
        final Collisions collisions = new Collisions(secuboid, WORLD, "land4", land4, Collisions.LandAction.LAND_REMOVE,
                0, null, null, PlayerContainerNobody.getInstance(), true, false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.HAS_CHILDREN)) {
            fail("Land has children not detected");
        }
    }

    @Test
    public void landNameInUse() throws SecuboidLandException {
        lands.createLand("land6", PlayerContainerNobody.getInstance(),
                new CuboidArea(true, WORLD, 3000, 0, 3000, 3100, 255, 3100));
        final Collisions collisions = new Collisions(secuboid, WORLD, "land6", null, Collisions.LandAction.LAND_ADD, 0,
                new CuboidArea(true, WORLD, 10, 0, 10, 120, 255, 120), null, PlayerContainerNobody.getInstance(), true,
                false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.NAME_IN_USE)) {
            fail("Land name in use not detected");
        }
    }

    @Test
    public void landMustHasOneArea() throws SecuboidLandException {
        final Collisions collisions = new Collisions(secuboid, WORLD, "land1", lands.getLand("land1"),
                Collisions.LandAction.AREA_REMOVE, 1, null, null, PlayerContainerNobody.getInstance(), true, false);
        collisions.doCollisionCheck();
        if (!isError(collisions, Collisions.LandError.MUST_HAVE_AT_LEAST_ONE_AREA)) {
            fail("Land must has at least one area not detected");
        }
    }
}
