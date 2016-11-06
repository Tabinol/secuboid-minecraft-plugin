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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author michel
 */
public class VisualSelectionLines implements VisualSelection {

    /**
     *
     */
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

    private final LinesArea area;
    //private Line curLine;
    private int x1;
    private int y1;
    private int z1;
    private int upDist;
    private int downDist;
    private int leftDist;
    private int rightDist;
    private boolean canCreate;

    /**
     *
     * @param area
     * @param isFromLand
     * @param player
     */
    public VisualSelectionLines(LinesArea area, boolean isFromLand, Player player) {
	changedBlocks = new ChangedBlocks(player);
	this.isFromLand = isFromLand;
	this.player = player;
	isCollision = false;
	parentDetected = null;
	Location loc = player.getLocation();
	if (area != null) {
	    this.area = area;
	} else {
	    this.area = null; // new LinesArea(loc.getWorld().getName(), null);
	}
	x1 = loc.getBlockX();
	y1 = loc.getBlockY();
	z1 = loc.getBlockZ();
	// TODO get correct dist
	this.upDist = 5;
	this.downDist = 5;
	this.leftDist = 5;
	this.rightDist = 5;
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

    /**
     *
     */
    @SuppressWarnings("deprecation")
    @Override
    public void removeSelection() {
	changedBlocks.resetBlocks();
    }

    /**
     *
     * @return
     */
    @Override
    public RealLand getParentDetected() {
	if (parentDetected.isRealLand()) {
	    return (RealLand) parentDetected;
	}
	return null;
    }

    /**
     *
     * @return
     */
    @Override
    public Area getArea() {

	return area;
    }

    @Override
    public void setActiveSelection() {

	isCollision = false;
	Location loc = player.getLocation();
	//curLine = new Line(x1, y1, z1, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
	//	upDist, downDist, leftDist, rightDist);

	makeVisualSelection();
    }

    @Override
    public void makeVisualSelection() {

	if (area.getLines().isEmpty()) {
	    // Detect the curent land from the 8 points
	    Land Land1 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
	    Land Land2 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY1(), area.getZ2()));
	    Land Land3 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY1(), area.getZ1()));
	    Land Land4 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY1(), area.getZ2()));
	    Land Land5 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY2(), area.getZ1()));
	    Land Land6 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX1(), area.getY2(), area.getZ2()));
	    Land Land7 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY2(), area.getZ1()));
	    Land Land8 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		    area.getWord(), area.getX2(), area.getY2(), area.getZ2()));

	    if (Land1 == Land2 && Land1 == Land3 && Land1 == Land4 && Land1 == Land5 && Land1 == Land6
		    && Land1 == Land7 && Land1 == Land8) {
		parentDetected = Land1;
	    } else {
		parentDetected = Secuboid.getThisPlugin().getLands().getOutsideArea(Land1.getWorldName());
	    }

	    canCreate = parentDetected.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());
	}

	//MakeLine
	if (!isFromLand) {
	    /*
	    // Active
	    int x1 = Calculate.lowerInt(curLine.getX1(), curLine.getX2());
	    int x2 = Calculate.greaterInt(curLine.getX1(), curLine.getX2());
	    int z1 = Calculate.lowerInt(curLine.getZ1(), curLine.getZ2());
	    int z2 = Calculate.greaterInt(curLine.getZ1(), curLine.getZ2());
	    for (int posX = x1; posX <= x2; posX++) {
		for (int posZ = z1; posZ <= z2; posZ++) {
		    int correctZ = (int) ((curLine.getA() * posX) + curLine.getB());
		    if (posZ == correctZ) {
			Location newloc = new Location(area.getWord(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1, posZ);
			Land testCuboidarea = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(newloc);
			if (parentDetected == testCuboidarea
				&& (canCreate || Secuboid.getThisPlugin().getPlayerConf().get(player).isAdminMode())) {
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_ACTIVE);
			} else {
			    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_COLLISION);
			    isCollision = true;
			}
		    }
		}
	    }
	     */
	} else {
	    /*
	    // Passive
	    Location newloc = new Location(area.getWord(), x1, PlayersUtil.getYNearPlayer(player, x1, z1) - 1, z1);
	    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE_CORNER);
	    newloc = new Location(area.getWord(), curLine.getX2(), PlayersUtil.getYNearPlayer(player, curLine.getX2(),
		    curLine.getZ2()) - 1, curLine.getZ2());
	    changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE_CORNER);
	     */
	}
    }

    /**
     *
     * @param moveType
     */
    @Override
    public void playerMove(AreaSelection.MoveType moveType) {

	removeSelection();
	setActiveSelection();
    }
}
