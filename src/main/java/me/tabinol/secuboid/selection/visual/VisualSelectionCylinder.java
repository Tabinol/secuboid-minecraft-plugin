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

import static java.lang.Math.abs;
import java.util.HashMap;
import java.util.Map;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author michel
 */
public class VisualSelectionCylinder implements VisualSelection {

    /**
     * The block list.
     */
    protected final Map<Location, Material> blockList;

    /**
     * The block byte (option) list.
     */
    protected final Map<Location, Byte> blockByteList;

    /**
     *
     */
    protected final Player player;

    /**
     * The is from land.
     */
    protected boolean isFromLand;

    /**
     * The is collision.
     */
    protected boolean isCollision;

    /**
     * Parent detected
     */
    protected Land parentDetected;

    private CylinderArea area;

    /**
     *
     * @param area
     * @param isFromLand
     * @param player
     */
    public VisualSelectionCylinder(CylinderArea area, boolean isFromLand, Player player) {
	blockList = new HashMap<Location, Material>();
	blockByteList = new HashMap<Location, Byte>();
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

    /**
     *
     */
    @SuppressWarnings("deprecation")
    @Override
    public void removeSelection() {

	for (Map.Entry<Location, Material> entrySet : this.blockList.entrySet()) {
	    this.player.sendBlockChange(entrySet.getKey(), entrySet.getValue(), blockByteList.get(entrySet.getKey()));
	}

	blockList.clear();
	blockByteList.clear();
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
	int landXr = Secuboid.getThisPlugin().getConf().getDefaultXSize() / 2;
	int landZr = Secuboid.getThisPlugin().getConf().getDefaultZSize() / 2;
	area = new CylinderArea(loc.getWorld().getName(),
		loc.getBlockX() - landXr, Secuboid.getThisPlugin().getConf().getDefaultBottom(), loc.getBlockZ() - landZr,
		loc.getBlockX() + landXr, Secuboid.getThisPlugin().getConf().getDefaultTop(), loc.getBlockZ() + landZr);

	makeVisualSelection();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void makeVisualSelection() {

	// Get the size (x and z) no abs (already ajusted)
	int diffX = area.getX2() - area.getX1();
	int diffZ = area.getZ2() - area.getZ1();

	// Do not show a too big select to avoid crash or severe lag
	int maxSize = Secuboid.getThisPlugin().getConf().getMaxVisualSelect();
	int maxDisPlayer = Secuboid.getThisPlugin().getConf().getMaxVisualSelectFromPlayer();
	Location playerLoc = player.getLocation();
	if (diffX > maxSize || diffZ > maxSize
		|| abs(area.getX1() - playerLoc.getBlockX()) > maxDisPlayer
		|| abs(area.getX2() - playerLoc.getBlockX()) > maxDisPlayer
		|| abs(area.getZ1() - playerLoc.getBlockZ()) > maxDisPlayer
		|| abs(area.getZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
	    Secuboid.getThisPlugin().getLog().write("Selection disabled!");
	    return;
	}

	// Detect the curent land from the 8 points
	Land Land1 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getX1(), area.getY1(), area.getOriginK()));
	Land Land2 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getOriginH(), area.getY1(), area.getZ1()));
	Land Land3 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getX2(), area.getY1(), area.getOriginK()));
	Land Land4 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getOriginH(), area.getY1(), area.getZ2()));
	Land Land5 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getX1(), area.getY2(), area.getOriginK()));
	Land Land6 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getOriginH(), area.getY2(), area.getZ1()));
	Land Land7 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getX2(), area.getY2(), area.getOriginK()));
	Land Land8 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
		area.getWord(), area.getOriginH(), area.getY2(), area.getZ2()));

	if (Land1 == Land2 && Land1 == Land3 && Land1 == Land4 && Land1 == Land5 && Land1 == Land6
		&& Land1 == Land7 && Land1 == Land8) {
	    parentDetected = Land1;
	} else {
	    parentDetected = Secuboid.getThisPlugin().getLands().getOutsideArea(Land1.getWorldName());
	}

	boolean canCreate = parentDetected.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

	//Make Cylinder
	for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
	    int zNeg = area.getZNegFromX(posX);
	    int zPos = area.getZPosFromX(posX);
	    int[] zPositions;
	    if (zNeg == zPos) {
		zPositions = new int[]{zNeg};
	    } else {
		zPositions = new int[]{zNeg, zPos};
	    }
	    for (int posZ : zPositions) {

		Location newloc = new Location(area.getWord(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1, posZ);
		Block block = newloc.getBlock();
		blockList.put(newloc, block.getType());
		blockByteList.put(newloc, block.getData());

		if (!isFromLand) {

		    // Active Selection
		    Land testCuboidarea = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(newloc);
		    if (parentDetected == testCuboidarea
			    && (canCreate == true || Secuboid.getThisPlugin().getPlayerConf().get(player).isAdminMode())) {
			this.player.sendBlockChange(newloc, Material.SPONGE, (byte) 0);
		    } else {
			this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, (byte) 0);
			isCollision = true;
		    }
		} else // Passive Selection (created area)
		{
		    if (posX == area.getOriginH() - 1
			    || posX == area.getOriginH() + 1
			    || posZ == area.getOriginK() - 1
			    || posZ == area.getOriginK() + 1) {

			// Subcorner
			this.player.sendBlockChange(newloc, Material.IRON_BLOCK, (byte) 0);

		    } else {

			// Exact corner
			this.player.sendBlockChange(newloc, Material.BEACON, (byte) 0);
		    }
		}
	    }
	}
    }

    /**
     *
     * @param moveType
     */
    @Override
    public void playerMove(AreaSelection.MoveType moveType) {

	switch (moveType) {
	    case ACTIVE:

		removeSelection();
		setActiveSelection();
		break;

	    case EXPAND:

		removeSelection();
		Location playerLoc = player.getLocation();

		// Check where the player is outside the land
		if (playerLoc.getBlockX() - 1 < area.getX1()) {
		    ((CylinderArea) area).setX1(playerLoc.getBlockX() - 1);
		}
		if (playerLoc.getBlockX() + 1 > area.getX2()) {
		    ((CylinderArea) area).setX2(playerLoc.getBlockX() + 1);
		}
		if (playerLoc.getBlockZ() - 1 < area.getZ1()) {
		    ((CylinderArea) area).setZ1(playerLoc.getBlockZ() - 1);
		}
		if (playerLoc.getBlockZ() + 1 > area.getZ2()) {
		    ((CylinderArea) area).setZ2(playerLoc.getBlockZ() + 1);
		}

		makeVisualSelection();
		break;

	    default:
	}
    }
}
