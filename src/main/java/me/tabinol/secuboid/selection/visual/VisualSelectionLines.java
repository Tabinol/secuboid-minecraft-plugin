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
package me.tabinol.secuboid.selection.visual;

import java.util.Collections;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.lands.areas.Point;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * The visual selection lines class.
 *
 * @author michel
 */
public class VisualSelectionLines implements VisualSelection {

    public static final int DEFAULT_UP = 5;
    public static final int DEFAULT_DOWN = 5;
    public static final int DEFAULT_RADIUS = 5;

    private final Secuboid secuboid;
    private final Player player;

    private final ChangedBlocks changedBlocks;

    /**
     * The is from land.
     */
    private final boolean isFromLand;

    /**
     * The is collision.
     */
    private boolean isCollision;

    /**
     * Parent detected
     */
    private Land parentDetected;

    private LinesArea area;

    public VisualSelectionLines(Secuboid secuboid, LinesArea area, boolean isFromLand, Player player) {
	this.secuboid = secuboid;
	changedBlocks = new ChangedBlocks(player);
	this.isFromLand = isFromLand;
	this.player = player;
	isCollision = false;
	parentDetected = null;
	this.area = area;
    }

    /**
     * Gets the collision.
     *
     * @return the collision
     */
    @Override
    public boolean hasCollision() {
	return isCollision;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeSelection() {
	changedBlocks.resetBlocks();
    }

    @Override
    public RealLand getParentDetected() {
	if (parentDetected.isRealLand()) {
	    return (RealLand) parentDetected;
	}
	return null;
    }

    @Override
    public Area getArea() {

	return area;
    }

    @Override
    public void setActiveSelection() {

	isCollision = false;

	Location loc = player.getLocation();
	int landXr = secuboid.getConf().getDefaultXSize() / 2;
	int landZr = secuboid.getConf().getDefaultZSize() / 2;
	area = new LinesArea(loc.getWorld().getName(), DEFAULT_UP, DEFAULT_DOWN, DEFAULT_RADIUS,
		Collections.singletonList(new Point(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));

	makeVisualSelection();
    }

    @Override
    public void makeVisualSelection() {

	if (area.getPoints().isEmpty()) {
	    // Detect the curent land from the 8 points
	    Land Land1 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
	    Land Land2 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY1(), area.getZ2()));
	    Land Land3 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY1(), area.getZ1()));
	    Land Land4 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY1(), area.getZ2()));
	    Land Land5 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY2(), area.getZ1()));
	    Land Land6 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY2(), area.getZ2()));
	    Land Land7 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY2(), area.getZ1()));
	    Land Land8 = secuboid.getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY2(), area.getZ2()));

	    if (Land1 == Land2 && Land1 == Land3 && Land1 == Land4 && Land1 == Land5 && Land1 == Land6
		    && Land1 == Land7 && Land1 == Land8) {
		parentDetected = Land1;
	    } else {
		parentDetected = secuboid.getLands().getOutsideArea(Land1.getWorldName());
	    }
	}

	boolean canCreate = parentDetected.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

	//MakeLines TODO ******************* IM HERE ************************
	for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
	    for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
		if (posX == area.getX1() || posX == area.getX2()
			|| posZ == area.getZ1() || posZ == area.getZ2()) {

		    Location newloc = new Location(area.getWord(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1, posZ);

		    if (!isFromLand) {

			// Active Selection
			Land testCuboidarea = secuboid.getLands().getLandOrOutsideArea(newloc);
			if (parentDetected == testCuboidarea
				&& (canCreate || secuboid.getPlayerConf().get(player).isAdminMode())) {
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_ACTIVE);
			} else {
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_COLLISION);
			    isCollision = true;
			}
		    } else // Passive Selection (created area)
		     if ((posX == area.getX1() && posZ == area.getZ1() + 1)
				|| (posX == area.getX1() && posZ == area.getZ2() - 1)
				|| (posX == area.getX2() && posZ == area.getZ1() + 1)
				|| (posX == area.getX2() && posZ == area.getZ2() - 1)
				|| (posX == area.getX1() + 1 && posZ == area.getZ1())
				|| (posX == area.getX2() - 1 && posZ == area.getZ1())
				|| (posX == area.getX1() + 1 && posZ == area.getZ2())
				|| (posX == area.getX2() - 1 && posZ == area.getZ2())) {

			    // Subcorner
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE_SUBCORNER);

			} else if ((posX == area.getX1() && posZ == area.getZ1())
				|| (posX == area.getX2() && posZ == area.getZ1())
				|| (posX == area.getX1() && posZ == area.getZ2())
				|| (posX == area.getX2() && posZ == area.getZ2())) {

			    // Exact corner
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE_CORNER);
			}

		} else {
		    // Square center, skip!
		    posZ = area.getZ2() - 1;
		}
	    }
	}
    }

    @Override
    public void playerMove(AreaSelection.MoveType moveType) {

	removeSelection();
	setActiveSelection();
    }
}
