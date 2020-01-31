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

import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.CHILD_OUT_OF_BORDER;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.COLLISION;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.HAS_CHILDREN;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.IN_APPROVE_LIST;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.MAX_AREA_FOR_LAND;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.MAX_LAND_FOR_PLAYER;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.MUST_HAVE_AT_LEAST_ONE_AREA;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.NAME_IN_USE;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.NOT_ENOUGH_MONEY;
import static me.tabinol.secuboid.lands.collisions.Collisions.LandError.OUT_OF_PARENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;

/**
 * The Class Collisions. This class is created for async calculation and price
 */
public final class Collisions {

    /**
     * The Enum LandError.
     */
    public enum LandError {
        COLLISION(true), NAME_IN_USE(false), HAS_CHILDREN(false), CHILD_OUT_OF_BORDER(true), OUT_OF_PARENT(true),
        IN_APPROVE_LIST(false), NOT_ENOUGH_MONEY(false), MAX_AREA_FOR_LAND(true), MAX_LAND_FOR_PLAYER(true),
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
        LandError(final boolean canBeApproved) {
            this.canBeApproved = canBeApproved;
        }
    }

    /**
     * The Enum LandAction.
     */
    public enum LandAction {
        LAND_ADD(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, NAME_IN_USE, NOT_ENOUGH_MONEY,
                MAX_LAND_FOR_PLAYER)),
        LAND_RENAME(EnumSet.of(IN_APPROVE_LIST, NAME_IN_USE)), LAND_REMOVE(EnumSet.of(IN_APPROVE_LIST, HAS_CHILDREN)),
        LAND_PARENT(EnumSet.of(IN_APPROVE_LIST, OUT_OF_PARENT)),
        AREA_ADD(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, NOT_ENOUGH_MONEY, MAX_AREA_FOR_LAND)),
        AREA_REMOVE(EnumSet.of(IN_APPROVE_LIST, CHILD_OUT_OF_BORDER, MUST_HAVE_AT_LEAST_ONE_AREA)),
        AREA_MODIFY(EnumSet.of(IN_APPROVE_LIST, COLLISION, OUT_OF_PARENT, CHILD_OUT_OF_BORDER, NOT_ENOUGH_MONEY));

        EnumSet<LandError> errorsToCheck;

        LandAction(final EnumSet<LandError> errorsToCheck) {
            this.errorsToCheck = errorsToCheck;
        }
    }

    private final Secuboid secuboid;
    private final String worldName;
    /**
     * The collisions entries.
     */
    private final List<CollisionsEntry> collisionsEntries;

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
    private final Land land;

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
    private final Land parent;

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
     * @param worldName        the world name
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
    public Collisions(final Secuboid secuboid, final String worldName, final String landName, final Land land,
            final LandAction action, final int removedAreaId, final Area newArea, final Land parent,
            final PlayerContainer owner, final boolean isFree, final boolean checkApproveList) {

        this.secuboid = secuboid;
        this.worldName = worldName;
        collisionsEntries = new ArrayList<>();
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

        percentDone = 0;
    }

    // Called from this package only
    void doCollisionCheck() {

        // Pass 1 & 2 check if there is a collision and is outside parent
        if (action.errorsToCheck.contains(COLLISION)) { // COLLISION check adds always OUT_OF_PARENT
            checkCollisionsAndInsideParent();
        }
        percentDone = 20;

        // Pass 3 check if children are not out of land
        if (action.errorsToCheck.contains(CHILD_OUT_OF_BORDER) && !land.getChildren().isEmpty()) {
            checkIfChildrenOutside();
        }
        percentDone = 30;

        // Pass 4 check if the deleted land has children
        if (action.errorsToCheck.contains(HAS_CHILDREN)) {
            checkIfLandHasChildren();
        }
        percentDone = 40;

        // Pass 5 check if the name is already existing
        if (landName != null && action.errorsToCheck.contains(NAME_IN_USE)) {
            checkIfNameExist();
        }
        percentDone = 50;

        // Pass 6 check if the name is already in Approve List (true in all actions!)
        if (landName != null && !checkApproveList && lands.getApproves().isInApprove(landName)) {
            collisionsEntries.add(new CollisionsEntry(secuboid, LandError.IN_APPROVE_LIST, null, 0));
        }
        percentDone = 60;

        if (owner.getContainerType() == PlayerContainerType.PLAYER) {

            // Pass 7 check if the player has enough money
            if (secuboid.getPlayerMoneyOpt().isPresent()) {
                final double price = getPrice();
                if ((price > 0) && newArea != null) {
                    final double playerBalance = secuboid.getPlayerMoneyOpt().get().getPlayerBalance(
                            ((PlayerContainerPlayer) owner).getOfflinePlayer(), newArea.getWorldName());
                    if (action.errorsToCheck.contains(NOT_ENOUGH_MONEY) && playerBalance < price) {
                        collisionsEntries.add(new CollisionsEntry(secuboid, LandError.NOT_ENOUGH_MONEY, null, 0));
                    }
                }
            }
            percentDone = 70;

            // Pass 8 check if the land has more than the maximum number of areas
            if (land != null && action.errorsToCheck.contains(MAX_AREA_FOR_LAND)
                    && land.getAreas().size() >= secuboid.getConf().getMaxAreaPerLand()) {
                collisionsEntries.add(new CollisionsEntry(secuboid, LandError.MAX_AREA_FOR_LAND, land, 0));
            }
            percentDone = 80;

            // Pass 9 check if the player has more than the maximum number of land
            if (action.errorsToCheck.contains(MAX_LAND_FOR_PLAYER)
                    && secuboid.getLands().getLands(owner).size() >= secuboid.getConf().getMaxLandPerPlayer()) {
                collisionsEntries.add(new CollisionsEntry(secuboid, LandError.MAX_LAND_FOR_PLAYER, null, 0));
            }
        }
        percentDone = 90;

        // Pass 10 check if the area to remove is the only one
        if (action.errorsToCheck.contains(MUST_HAVE_AT_LEAST_ONE_AREA) && land != null && land.getAreas().size() == 1) {
            collisionsEntries
                    .add(new CollisionsEntry(secuboid, LandError.MUST_HAVE_AT_LEAST_ONE_AREA, land, removedAreaId));
        }
        percentDone = 100;

        // End check if the action can be done or approve
        allowApprove = true;
        for (final CollisionsEntry entry : collisionsEntries) {
            if (!entry.getError().canBeApproved) {
                allowApprove = false;
                return;
            }
        }
    }

    /**
     * Check collisions.
     */
    private void checkCollisionsAndInsideParent() {
        final HashSet<Area> landCollisionsList = new HashSet<>();
        boolean outsideParent = false;

        for (int x = newArea.getX1(); x <= newArea.getX2(); x++) {
            for (int z = newArea.getZ1(); z <= newArea.getZ2(); z++) {
                if (newArea.isLocationInside(worldName, x, z)) {
                    for (final Area area2 : lands.getAreas(worldName, x, z)) {
                        final Land land2 = area2.getLand();
                        if (land != land2 && !isDescendants(land, land2) && !isDescendants(land2, parent)) {
                            landCollisionsList.add(area2);
                        }
                    }
                }
                if (parent != null && !lands.getLands(worldName, x, z).contains(parent)) {
                    outsideParent = true;
                }
            }
        }

        for (final Area areaCol : landCollisionsList) {
            collisionsEntries.add(new CollisionsEntry(secuboid, COLLISION, areaCol.getLand(), areaCol.getKey()));
        }

        if (outsideParent) {
            collisionsEntries.add(new CollisionsEntry(secuboid, OUT_OF_PARENT, parent, 0));
        }
    }

    /**
     * Checks if is descendants.
     *
     * @param land1 the land1
     * @param land2 the land2
     * @return true, if is descendants
     */
    private boolean isDescendants(final Land land1, final Land land2) {
        return !(land1 == null || land2 == null) && land1.isDescendants(land2);
    }

    /**
     * Check if children outside.
     */
    private void checkIfChildrenOutside() {
        final HashSet<Land> childOutsideLand = new HashSet<>();
        final HashSet<Area> areaList = new HashSet<>();

        if (action != LandAction.LAND_REMOVE) {
            for (final Area area : land.getAreas()) {
                if (area.getKey() != removedAreaId) {
                    areaList.add(area);
                }
            }
        }
        if (newArea != null) {
            areaList.add(newArea);
        }

        for (final Area areaC : areaList) {
            for (int x = areaC.getX1(); x <= areaC.getX2(); x++) {
                for (int z = areaC.getZ1(); z <= areaC.getZ2(); z++) {
                    if (areaC.isLocationInside(worldName, x, z)) {
                        for (final Land child : land.getChildren()) {
                            if (!child.isLocationInside(worldName, x, z)) {
                                childOutsideLand.add(child);
                            }
                        }
                    }
                }
            }
        }

        for (final Land child : childOutsideLand) {
            collisionsEntries.add(new CollisionsEntry(secuboid, CHILD_OUT_OF_BORDER, child, 0));
        }
    }

    /**
     * Check if land has children.
     */
    private void checkIfLandHasChildren() {
        for (final Land child : land.getChildren()) {
            collisionsEntries.add(new CollisionsEntry(secuboid, LandError.HAS_CHILDREN, child, 0));
        }
    }

    /**
     * Check if name exist.
     */
    private void checkIfNameExist() {
        if (lands.isNameExist(landName)) {
            collisionsEntries.add(new CollisionsEntry(secuboid, NAME_IN_USE, null, 0));
        }
    }

    /**
     * Gets the prints.
     *
     * @return the prints
     */
    public String getPrints() {
        final StringBuilder str = new StringBuilder();

        for (final CollisionsEntry ce : collisionsEntries) {
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
        return !collisionsEntries.isEmpty();
    }

    /**
     * Gets land collisions.
     *
     * @return the land collisions
     */
    public Collection<CollisionsEntry> getCollisions() {
        return collisionsEntries;
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

    public Land getLand() {
        return land;
    }

    public String getLandName() {
        return landName;
    }

    /**
     * Gets the price of the land. If there is no new area, it returns 0.
     *
     * @return the land price
     */
    public double getPrice() {
        double priceFlag;
        double price;

        if (newArea == null || !secuboid.getConf().useEconomy()) {
            return 0;
        }

        final FlagType flagType = FlagList.ECO_BLOCK_PRICE.getFlagType();
        if (land == null) {
            priceFlag = secuboid.getLands().getOutsideLandPermissionsFlags(newArea.getWorldName())
                    .getFlagAndInherit(flagType).getValueDouble();
        } else {
            if (land.getParent() == null) {
                priceFlag = secuboid.getLands().getOutsideLandPermissionsFlags(newArea.getWorldName())
                        .getFlagAndInherit(flagType).getValueDouble();
            } else {
                priceFlag = land.getParent().getPermissionsFlags().getFlagAndInherit(flagType).getValueDouble();
            }
        }

        price = priceFlag * newArea.getArea();
        if (removedAreaId != 0 && land != null) {
            price -= land.getArea(removedAreaId).getArea();
        }

        return price < 0 ? 0 : price;
    }

    /**
     * Gets the number of percent done for collision steps.
     *
     * @return the number on percent (100 = 100%)
     */
    public synchronized int getPercentDone() {
        return percentDone;
    }

    /**
     * Gets the action requested (CREATE, REMOVE, etc.)
     *
     * @return the land action
     */
    public LandAction getAction() {
        return action;
    }
}
