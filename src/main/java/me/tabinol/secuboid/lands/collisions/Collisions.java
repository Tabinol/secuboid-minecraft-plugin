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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;

/**
 * The Class Collisions. This class is created for async calculation and price
 */
public class Collisions {

    /**
     * The Enum LandAction.
     */
    public enum LandAction {

	/**
	 * The land add.
	 */
	LAND_ADD,
	/**
	 * The land rename.
	 */
	LAND_RENAME,
	/**
	 * The land remove.
	 */
	LAND_REMOVE,
	/**
	 * The land parent.
	 */
	LAND_PARENT,
	/**
	 * The area add.
	 */
	AREA_ADD,
	/**
	 * The area remove.
	 */
	AREA_REMOVE,
	/**
	 * The area modify.
	 */
	AREA_MODIFY
    }

    /**
     * The Enum LandError.
     */
    public enum LandError {

	/**
	 * The collision.
	 */
	COLLISION(true),
	/**
	 * The name in use.
	 */
	NAME_IN_USE(false),
	/**
	 * The has children.
	 */
	HAS_CHILDREN(false),
	/**
	 * The child out of border.
	 */
	CHILD_OUT_OF_BORDER(true),
	/**
	 * The out of parent.
	 */
	OUT_OF_PARENT(true),
	/**
	 * The in approve list.
	 */
	IN_APPROVE_LIST(false),
	/**
	 * The not enough money.
	 */
	NOT_ENOUGH_MONEY(false),
	/**
	 * The max area for land.
	 */
	MAX_AREA_FOR_LAND(true),
	/**
	 * The max land for player.
	 */
	MAX_LAND_FOR_PLAYER(true),
	/**
	 * A land must have one or more areas
	 */
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
     * Instantiates a new collisions.
     *
     * @param secuboid secuboid instance
     * @param landName the land name
     * @param land the land
     * @param action the action
     * @param removedAreaId the removed area id
     * @param newArea the new area
     * @param parent the parent
     * @param owner the owner
     * @param isFree if the land is free or not
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
    }

    // Called from this package only
    void doCollisionCheck() {

	// Pass 0 get collision information for the next passes
	if (action != LandAction.LAND_RENAME) {
	    doDotByDotCollisionInfo();
	}

	// Pass 1 check if there is a collision
	if (action == LandAction.LAND_ADD || action == LandAction.AREA_ADD || action == LandAction.AREA_MODIFY) {
	    checkCollisions();
	}

	// Pass 2 check if the the cuboid is inside the parent
	if (parent != null) {
	    if (action == LandAction.LAND_ADD || action == LandAction.AREA_ADD
		    || action == LandAction.AREA_MODIFY || action == LandAction.LAND_PARENT) {
		checkIfInsideParent();
	    }
	}

	// Pass 3 check if children are not out of land
	if ((action == LandAction.AREA_MODIFY || action == LandAction.AREA_REMOVE)
		&& !land.getChildren().isEmpty()) {
	    checkIfChildrenOutside();
	}

	// Pass 4 check if the deleted land has children
	if (action == LandAction.LAND_REMOVE) {
	    checkIfLandHasChildren();
	}

	// Pass 5 check if the name is already existing
	if (landName != null && (action == LandAction.LAND_ADD || action == LandAction.LAND_RENAME)) {
	    checkIfNameExist();
	}

	// Pass 6 check if the name is already in Approve List
	if (landName != null && !checkApproveList
		&& ((me.tabinol.secuboid.lands.Lands) lands).getApproveList().isInApprove(landName)) {
	    coll.add(new CollisionsEntry(secuboid, LandError.IN_APPROVE_LIST, null, 0));
	}

	if (owner.getContainerType() == PlayerContainerType.PLAYER) {

	    // Pass 7 check if the player has enough money
	    if ((priceArea > 0 || priceLand > 0) && newArea != null) {
		double playerBalance = secuboid.getPlayerMoney().getPlayerBalance(
			((PlayerContainerPlayer) owner).getOfflinePlayer(), newArea.getWorldName());
		if (action == LandAction.LAND_ADD) {
		    if ((action == LandAction.LAND_ADD && playerBalance < priceLand) || playerBalance < priceArea) {
			coll.add(new CollisionsEntry(secuboid, LandError.NOT_ENOUGH_MONEY, null, 0));
		    }
		}
	    }

	    // Pass 8 check if the land has more than the maximum number of areas
	    if (land != null && action == LandAction.AREA_ADD
		    && land.getAreas().size() >= secuboid.getConf().getMaxAreaPerLand()) {
		coll.add(new CollisionsEntry(secuboid, LandError.MAX_AREA_FOR_LAND, land, 0));
	    }

	    // Pass 9 check if the player has more than the maximum number of land
	    if (action == LandAction.LAND_ADD
		    && secuboid.getLands().getLands(owner).size() >= secuboid.getConf().getMaxLandPerPlayer()) {
		coll.add(new CollisionsEntry(secuboid, LandError.MAX_LAND_FOR_PLAYER, null, 0));
	    }
	}

	// Pass 10 check if the area to remove is the only one
	if (action == LandAction.AREA_REMOVE && land.getAreas().size() == 1) {
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
	FlagType flagType = FlagList.ECO_BLOCK_PRICE.getFlagType();
	if (newArea != null) {
	    if (land == null) {
		priceLandFlag = secuboid.getLands().getOutsideArea(newArea.getWorldName()).getPermissionsFlags()
			.getFlagAndInherit(flagType).getValueDouble();

	    } else {
		priceLandFlag = land.getPermissionsFlags().getFlagAndInherit(flagType).getValueDouble();
	    }
	    if (land != null) {
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
	    int x1 = Integer.MIN_VALUE;
	    int y1 = Integer.MIN_VALUE;
	    int z1 = Integer.MIN_VALUE;
	    int x2 = Integer.MAX_VALUE;
	    int y2 = Integer.MAX_VALUE;
	    int z2 = Integer.MAX_VALUE;
	    for (Area area : land.getAreas()) {
		x1 = Calculate.lowerInt(area.getX1(), x1);
		y1 = Calculate.lowerInt(area.getY1(), y1);
		z1 = Calculate.lowerInt(area.getZ1(), z1);
		x2 = Calculate.greaterInt(area.getX2(), x2);
		y2 = Calculate.greaterInt(area.getY2(), y2);
		z2 = Calculate.greaterInt(area.getZ2(), z2);
	    }
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
		    }
		}
	    }
	}

	// The new area
	if (newArea != null && land != null) {
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
				if (land == null || !lands2.contains(land)) {
				    priceArea += priceAreaFlag;
				}
			    }
			}
		    }
		}
	    }
	}

	//Children outside
	if (land != null) {
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
	    }
	}
    }

    /**
     * Check collisions.
     */
    private void checkCollisions() {

	for (Area areaCol : landCollisionsList) {
	    coll.add(new CollisionsEntry(secuboid, LandError.COLLISION, areaCol.getLand(), areaCol.getKey()));
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

	if (land1 == null || land2 == null) {
	    return false;
	}
	return land1.isDescendants(land2);
    }

    /**
     * Check if inside parent and adds an error if not.
     *
     * @return true if inside the parent
     */
    private boolean checkIfInsideParent() {

	if (outsideParent) {
	    coll.add(new CollisionsEntry(secuboid, LandError.OUT_OF_PARENT, parent, 0));
	    return false;
	}

	return true;
    }

    /**
     * Check if children outside.
     */
    private void checkIfChildrenOutside() {

	for (RealLand child : childOutsideLand) {
	    coll.add(new CollisionsEntry(secuboid, LandError.CHILD_OUT_OF_BORDER, child, 0));
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
	    coll.add(new CollisionsEntry(secuboid, LandError.NAME_IN_USE, null, 0));
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
     * Gets the entries.
     *
     * @return the entries
     */
    public Collection<CollisionsEntry> getEntries() {

	return Collections.unmodifiableCollection(coll);
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

    /**
     *
     * @return
     */
    public String getLandName() {

	return landName;
    }

    /**
     *
     * @return
     */
    public double getPriceLand() {

	return priceLand;
    }

    /**
     *
     * @return
     */
    public double getPriceArea() {

	return priceArea;
    }

    /**
     *
     * @return
     */
    public double getPrice() {

	if (action == LandAction.LAND_ADD) {
	    return priceLand;
	}

	return priceArea;
    }
}
