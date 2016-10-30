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
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.Calculate;
import me.tabinol.secuboid.utilities.PlayersUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author michel
 */
public class VisualSelectionLines implements VisualSelection {

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

    private final LinesArea area;
    private LineLine curLine;
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

	blockList = new HashMap<Location, Material>();
	blockByteList = new HashMap<Location, Byte>();
	this.isFromLand = isFromLand;
	this.player = player;
	isCollision = false;
	parentDetected = null;
	Location loc = player.getLocation();
	if (area != null) {
	    this.area = area;
	} else {
	    this.area = new LinesArea(loc.getWorld().getName(), null);
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
     * Create a new visual selection from default
     *
     * @param areaType areaType
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(AreaType areaType, boolean isFromLand, Player player) {

	if (areaType == AreaType.CUBOID) {
	    return new VisualSelectionCuboid(null, isFromLand, player);
	}
	return new VisualSelectionCylinder(null, isFromLand, player);
    }

    /**
     * Create a visual selection from an area
     *
     * @param area area
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(Area area,
	    boolean isFromLand, Player player) {

	if (area.getAreaType() == AreaType.CUBOID) {
	    return new VisualSelectionCuboid((CuboidArea) area, isFromLand, player);
	}
	return new VisualSelectionCylinder((CylinderArea) area, isFromLand, player);
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
	curLine = new LineLine(x1, y1, z1, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
		upDist, downDist, leftDist, rightDist);

	makeVisualSelection();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void makeVisualSelection() {

	// Get the size (x and z) no abs (already ajusted)
	int diffX = abs(curLine.getLeftX1() - curLine.getRightX2());
	int diffZ = abs(curLine.getLeftZ1() - curLine.getRightZ2());

	// Do not show a too big select to avoid crash or severe lag
	int maxSize = Secuboid.getThisPlugin().getConf().getMaxVisualSelect();
	int maxDisPlayer = Secuboid.getThisPlugin().getConf().getMaxVisualSelectFromPlayer();
	Location playerLoc = player.getLocation();
	if (diffX > maxSize || diffZ > maxSize
		|| abs(curLine.getLeftX1() - playerLoc.getBlockX()) > maxDisPlayer
		|| abs(curLine.getRightX2() - playerLoc.getBlockX()) > maxDisPlayer
		|| abs(curLine.getLeftZ1() - playerLoc.getBlockZ()) > maxDisPlayer
		|| abs(curLine.getRightZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
	    Secuboid.getThisPlugin().getLog().write("Selection disabled!");
	    return;
	}

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
			Block block = newloc.getBlock();
			blockList.put(newloc, block.getType());
			blockByteList.put(newloc, block.getData());
			Land testCuboidarea = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(newloc);
			if (parentDetected == testCuboidarea
				&& (canCreate || Secuboid.getThisPlugin().getPlayerConf().get(player).isAdminMode())) {
			    this.player.sendBlockChange(newloc, Material.SPONGE, (byte) 0);
			} else {
			    this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, (byte) 0);
			    isCollision = true;
			}
		    }
		}
	    }
	} else {
	    // Passive
	    Location newloc = new Location(area.getWord(), x1, PlayersUtil.getYNearPlayer(player, x1, z1) - 1, z1);
	    Block block = newloc.getBlock();
	    blockList.put(newloc, block.getType());
	    blockByteList.put(newloc, block.getData());
	    this.player.sendBlockChange(newloc, Material.BEACON, (byte) 0);
	    newloc = new Location(area.getWord(), curLine.getX2(), PlayersUtil.getYNearPlayer(player, curLine.getX2(),
		    curLine.getZ2()) - 1, curLine.getZ2());
	    block = newloc.getBlock();
	    blockList.put(newloc, block.getType());
	    blockByteList.put(newloc, block.getData());
	    this.player.sendBlockChange(newloc, Material.BEACON, (byte) 0);
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
