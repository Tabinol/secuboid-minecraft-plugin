/*
< Secuboid: Lands and Protection plugin for Minecraft server
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

import java.util.*;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.utilities.LocalMath;
import org.bukkit.Location;

import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.*;

/**
 * The Class Collisions. This class is created for async calculation and price
 */
public class Collisions {

    /**
     * The Enum LandError.
     */
    public enum LandError {
        COLLISION(true),
        NAME_IN_USE(false),
        HAS_CHILDREN(false),
        CHILD_OUT_OF_BORDER(true),
        OUT_OF_PARENT(true),
        IN_APPROVE_LIST(false),
        NOT_ENOUGH_MONEY(false),
        MAX_AREA_FOR_LAND(true),
        MAX_LAND_FOR_PLAYER(true),
        MUST_HAVE_AT_LEAST_ONE_AREA(false);

        /**
         * The can be approved.
         */
        final boolean canBeApproved; // False = No approve is possible

        /**
         * Instantiates a new land error.
         *
         * @param canBeApproved the can be approved
         */
        LandError(boolean canBeApproved) {
            this.canBeApproved = canBeApproved;
        }
    }

    /**
     * The Enum LandAction.
     */
    public enum LandAction {
        LAND_ADD(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, NAME_IN_USE, NOT_ENOUGH_MONEY, MAX_LAND_FOR_PLAYER)),
        LAND_RENAME(EnumSet.of(IN_APPROVE_LIST, NAME_IN_USE)),
        LAND_REMOVE(EnumSet.of(IN_APPROVE_LIST)),
        LAND_PARENT(EnumSet.of(IN_APPROVE_LIST, OUT_OF_PARENT)),
        AREA_ADD(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, NOT_ENOUGH_MONEY, MAX_AREA_FOR_LAND)),
        AREA_REMOVE(EnumSet.of(IN_APPROVE_LIST, CHILD_OUT_OF_BORDER, MUST_HAVE_AT_LEAST_ONE_AREA)),
        AREA_MODIFY(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, CHILD_OUT_OF_BORDER, NOT_ENOUGH_MONEY));

        EnumSet<LandError> errorsToCheck;

        LandAction(EnumSet<LandError> errorsToCheck) {
            this.errorsToCheck = errorsToCheck;
        }
    }

    private final Secuboid secuboid;

    /**
     * The coll.
     */
    private final List<CollisionsEntry> coll;

    /**
     * The lands.
     */
    private final Lands lands;

    /**
     * The land name.
     */
    private final String landName;

    /**
     * The land.
     */
    private final RealLand land;

    /**
     * The action.
     */
    private final LandAction action;

    /**
     * The removed area id.
     */
    private final int removedAreaId;

    /**
     * The new area.
     */
    private final Area newArea;

    /**
     * The parent.
     */
    private final RealLand parent;

    /**
     * The owner
     */
    private final PlayerContainer owner;

    /**
     * Is free
     */
    private final boolean isFree;

    /**
     * Check approve list
     */
    private final boolean checkApproveList;

    private final HashSet<Area> landCollisionsList;
    private final HashSet<RealLand> childOutsideLand;
    private boolean outsideParent;
    private double priceArea;
    private double priceLand;

    /**
     * The allow approve.
     */
    private boolean allowApprove;

    /**
     * Collision percentage done
     */
    private int percentDone;

    /**
     * Instantiates a new collisions.
     *
     * @param secuboid         secuboid instance
     * @param landName         the land name
     * @param land             the land
     * @param action           the action
     * @param removedAreaId    the removed area id
     * @param newArea          the new area
     * @param parent           the parent
     * @param owner            the owner
     * @param isFree           if the land is free or not
     * @param checkApproveList the check approve list
     */
    public Collisions(Secuboid secuboid, String landName, RealLand land, LandAction action, int removedAreaId, Area newArea,
                      RealLand parent, PlayerContainer owner, boolean isFree, boolean checkApproveList) {

        this.secuboid = secuboid;
        coll = new ArrayList<CollisionsEntry>();
        lands = secuboid.getLands();
        this.landName = landName;
        this.land = land;
        this.action = action;
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.parent = parent;
        this.owner = owner;
        this.isFree = isFree;
        this.checkApproveList = checkApproveList;

        landCollisionsList = new HashSet<Area>();
        childOutsideLand = new HashSet<RealLand>();
        outsideParent = false;
        priceArea = 0;
        priceLand = 0;
        percentDone = 0;
    }

    // Called from this package only
    void doCollisionCheck() {

        // Pass 0 get collision information for the next passes
        if (action != LandAction.LAND_RENAME) {
            doDotByDotCollisionInfo();
        }

        // Pass 1 check if there is a collision
        if (action.errorsToCheck.contains(COLLISION)) {
            checkCollisions();
        }

        // Pass 2 check if the the cuboid is inside the parent
        if (parent != null) {
            if (action.errorsToCheck.contains(OUT_OF_PARENT)) {
                checkIfInsideParent();
            }
        }

        // Pass 3 check if children are not out of land
        if (action.errorsToCheck.contains(CHILD_OUT_OF_BORDER)
                && !land.getChildren().isEmpty()) {
            checkIfChildrenOutside();
        }

        // Pass 4 check if the deleted land has children
        if (action.errorsToCheck.contains(HAS_CHILDREN)) {
            checkIfLandHasChildren();
        }

        // Pass 5 check if the name is already existing
        if (landName != null && action.errorsToCheck.contains(NAME_IN_USE)) {
            checkIfNameExist();
        }

        // Pass 6 check if the name is already in Approve List (true in all actions!)
        if (landName != null && !checkApproveList && lands.getApproveList().isInApprove(landName)) {
            coll.add(new CollisionsEntry(secuboid, LandError.IN_APPROVE_LIST, null, 0));
        }

        if (owner.getContainerType() == PlayerContainerType.PLAYER) {

            // Pass 7 check if the player has enough money
            if ((priceArea > 0 || priceLand > 0) && newArea != null) {
                double playerBalance = secuboid.getPlayerMoney().getPlayerBalance(
                        ((PlayerContainerPlayer) owner).getOfflinePlayer(), newArea.getWorldName());
                if (action.errorsToCheck.contains(NOT_ENOUGH_MONEY) && (playerBalance < priceLand || playerBalance < priceArea)) {
                    coll.add(new CollisionsEntry(secuboid, LandError.NOT_ENOUGH_MONEY, null, 0));
                }
            }

            // Pass 8 check if the land has more than the maximum number of areas
            if (land != null && action.errorsToCheck.contains(MAX_AREA_FOR_LAND)
                    && land.getAreas().size() >= secuboid.getConf().getMaxAreaPerLand()) {
                coll.add(new CollisionsEntry(secuboid, LandError.MAX_AREA_FOR_LAND, land, 0));
            }

            // Pass 9 check if the player has more than the maximum number of land
            if (action.errorsToCheck.contains(MAX_LAND_FOR_PLAYER)
                    && secuboid.getLands().getLands(owner).size() >= secuboid.getConf().getMaxLandPerPlayer()) {
                coll.add(new CollisionsEntry(secuboid, LandError.MAX_LAND_FOR_PLAYER, null, 0));
            }
        }

        // Pass 10 check if the area to remove is the only one
        if (action.errorsToCheck.contains(MUST_HAVE_AT_LEAST_ONE_AREA) && land != null && land.getAreas().size() == 1) {
            coll.add(new CollisionsEntry(secuboid, LandError.MUST_HAVE_AT_LEAST_ONE_AREA, land, removedAreaId));
        }

        // End check if the action can be done or approve
        allowApprove = true;
        for (CollisionsEntry entry : coll) {
            if (!entry.getError().canBeApproved) {
                allowApprove = false;
                return;
            }
        }
    }

    private void doDotByDotCollisionInfo() {

        // Prepare for land calculation
        double priceLandFlag = 0;
        double priceAreaFlag = 0;
        int nbSteps;
        int completedSteps = 0;

        // Gets Number of steps
        if (land != null) {
            if (newArea != null) {
                nbSteps = 3;
            } else {
                nbSteps = 2;
            }
        } else {
            nbSteps = 0;
        }

        FlagType flagType = FlagList.ECO_BLOCK_PRICE.getFlagType();
        if (newArea != null) {
            if (land == null) {
                priceLandFlag = secuboid.getLands().getOutsideArea(newArea.getWorldName()).getPermissionsFlags()
                        .getFlagAndInherit(flagType).getValueDouble();

            } else {
                if (land.getParent() == null) {
                    priceAreaFlag = secuboid.getLands().getOutsideArea(newArea.getWorldName()).getPermissionsFlags()
                            .getFlagAndInherit(flagType).getValueDouble();
                } else {
                    priceAreaFlag = land.getParent().getPermissionsFlags().getFlagAndInherit(flagType).getValueDouble();
                }
            }
        }

        // Loop for Land first
        if (land != null) {
            int x1 = Integer.MAX_VALUE;
            int y1 = Integer.MAX_VALUE;
            int z1 = Integer.MAX_VALUE;
            int x2 = Integer.MIN_VALUE;
            int y2 = Integer.MIN_VALUE;
            int z2 = Integer.MIN_VALUE;
            for (Area area : land.getAreas()) {
                x1 = LocalMath.lowerInt(area.getX1(), x1);
                y1 = LocalMath.lowerInt(area.getY1(), y1);
                z1 = LocalMath.lowerInt(area.getZ1(), z1);
                x2 = LocalMath.greaterInt(area.getX2(), x2);
                y2 = LocalMath.greaterInt(area.getY2(), y2);
                z2 = LocalMath.greaterInt(area.getZ2(), z2);
            }
            long points = (x2 - x1) * (y2 - y1) * (z2 - z1);
            long i = 0;
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        Location loc = new Location(land.getWorld(), x, y, z);
                        if (land.isLocationInside(loc)) {
                            for (Area area2 : lands.getAreas(loc)) {
                                RealLand land2 = area2.getLand();
                                if (land != land2 && !isDescendants(land, land2) && !isDescendants(land2, parent)) {
                                    landCollisionsList.add(area2);
                                }
                            }
                            if (!lands.getLands(loc).contains(parent)) {
                                outsideParent = true;
                            }
                        }
                        // Completed steps
                        i++;
                        percentDone = (int) ((i / points / nbSteps + (completedSteps / nbSteps)) * 95);
                    }
                }
            }
            completedSteps++;
        }

        // The new area
        if (newArea != null && land != null) {
            long points = (newArea.getX2() - newArea.getX1()) * (newArea.getY2() - newArea.getY1())
                    * (newArea.getZ2() - newArea.getZ1());
            long i = 0;
            for (int x = newArea.getX1(); x <= newArea.getX2(); x++) {
                for (int y = newArea.getY1(); y <= newArea.getY2(); y++) {
                    for (int z = newArea.getZ1(); z <= newArea.getZ2(); z++) {
                        Location loc = new Location(land.getWorld(), x, y, z);
                        if (newArea.isLocationInside(loc)) {
                            Collection<RealLand> lands2 = lands.getLands(loc);
                            if (parent != null && !lands2.contains(parent)) {
                                outsideParent = true;
                            }
                            if (!isFree) {
                                priceLand += priceLandFlag;
                                if (!lands2.contains(land)) {
                                    priceArea += priceAreaFlag;
                                }
                            }
                        }
                        // Completed steps
                        i++;
                        percentDone = (int) ((i / points / nbSteps + (completedSteps / nbSteps)) * 95);
                    }
                }
            }
            completedSteps++;
        }

        //Children outside
        if (land != null) {
            long i = 0;
            HashSet<Area> areaList = new HashSet<Area>();

            if (action != LandAction.LAND_REMOVE) {
                for (Area area : land.getAreas()) {
                    if (area.getKey() != removedAreaId) {
                        areaList.add(area);
                    }
                }
            }
            if (newArea != null) {
                areaList.add(newArea);
            }

            int points = areaList.size();
            for (Area areaC : areaList) {
                for (int x = areaC.getX1(); x <= areaC.getX2(); x++) {
                    for (int y = areaC.getY1(); y <= areaC.getY2(); y++) {
                        for (int z = areaC.getZ1(); z <= areaC.getZ2(); z++) {
                            Location loc = new Location(land.getWorld(), x, y, z);
                            if (areaC.isLocationInside(loc)) {
                                for (RealLand child : land.getChildren()) {
                                    if (!child.isLocationInside(loc)) {
                                        childOutsideLand.add(child);
                                    }
                                }
                            }
                        }
                    }
                }
                // Completed steps
                i++;
                percentDone = (int) ((i / points / nbSteps + (completedSteps / nbSteps)) * 95);
            }
        }
    }

    /**
     * Check collisions.
     */
    private void checkCollisions() {
        for (Area areaCol : landCollisionsList) {
            coll.add(new CollisionsEntry(secuboid, COLLISION, areaCol.getLand(), areaCol.getKey()));
        }
    }

    /**
     * Checks if is descendants.
     *
     * @param land1 the land1
     * @param land2 the land2
     * @return true, if is descendants
     */
    private boolean isDescendants(RealLand land1, RealLand land2) {
        return !(land1 == null || land2 == null) && land1.isDescendants(land2);
    }

    /**
     * Check if inside parent and adds an error if not.
     *
     * @return true if inside the parent
     */
    private boolean checkIfInsideParent() {
        if (outsideParent) {
            coll.add(new CollisionsEntry(secuboid, OUT_OF_PARENT, parent, 0));
            return false;
        }
        return true;
    }

    /**
     * Check if children outside.
     */
    private void checkIfChildrenOutside() {
        for (RealLand child : childOutsideLand) {
            coll.add(new CollisionsEntry(secuboid, CHILD_OUT_OF_BORDER, child, 0));
        }
    }

    /**
     * Check if land has children.
     */
    private void checkIfLandHasChildren() {
        for (RealLand child : land.getChildren()) {
            coll.add(new CollisionsEntry(secuboid, LandError.HAS_CHILDREN, child, 0));
        }
    }

    /**
     * Check if name exist.
     */
    private void checkIfNameExist() {

        if (lands.isNameExist(landName)) {
            coll.add(new CollisionsEntry(secuboid, NAME_IN_USE, null, 0));
        }
    }

    /**
     * Gets the prints.
     *
     * @return the prints
     */
    public String getPrints() {
        StringBuilder str = new StringBuilder();

        for (CollisionsEntry ce : coll) {
            str.append(ce.getPrint()).append(Config.NEWLINE);
        }
        return str.toString();
    }

    /**
     * Checks for collisions.
     *
     * @return true, if successful
     */
    public boolean hasCollisions() {
        return coll.size() > 0;
    }

    /**
     * Gets the allow approve.
     *
     * @return the allow approve
     */
    public boolean getAllowApprove() {
        return allowApprove;
    }

    /**
     * Is the player free?
     *
     * @return false if the player must pay
     */
    public boolean isFree() {
        return isFree;
    }

    public String getLandName() {
        return landName;
    }

    public double getPriceLand() {
        return priceLand;
    }

    public double getPriceArea() {
        return priceArea;
    }

    public double getPrice() {
        if (action == LandAction.LAND_ADD) {
            return priceLand;
        }
        return priceArea;
    }

    /**
     * Gets the number of percent done for collision steps.
     *
     * @return the number on percent
     */
    public synchronized int getPercentDone() {
        return percentDone;
    }
}
