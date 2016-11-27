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
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * The visual selection cuboid class.
 */
public class VisualSelectionCuboid implements VisualSelection {

    private final Secuboid secuboid;
    private final VisualCommon visualCommon;

    /**
     * The player.
     */
    private final Player player;

    private final ChangedBlocks changedBlocks;

    /**
     * Is from land.
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

    private CuboidArea area;

    public VisualSelectionCuboid(Secuboid secuboid, CuboidArea area, boolean isFromLand, Player player) {
        this.secuboid = secuboid;
        if (area == null) {
            visualCommon = new VisualCommon(secuboid, this, player,
                    secuboid.getPlayerConf().get(player), player.getLocation());
        } else {
            visualCommon = new VisualCommon(secuboid, this, player,
                    secuboid.getPlayerConf().get(player), area.getY1(), area.getY2());
        }
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
        area = new CuboidArea(loc.getWorld().getName(),
                loc.getBlockX() - landXr, visualCommon.getY1(), loc.getBlockZ() - landZr,
                loc.getBlockX() + landXr, visualCommon.getY2(), loc.getBlockZ() + landZr);

        makeVisualSelection();
    }

    @Override
    public void makeVisualSelection() {

        // Detect the current land from the 8 points
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

        boolean canCreate = parentDetected.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

        //MakeSquare
        int stepX = visualCommon.getStepX(area);
        int stepZ = visualCommon.getStepZ(area);
        for (int posX = area.getX1(); posX <= area.getX2(); posX += stepX) {
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ += stepZ) {
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
                    } else {
                        // Passive Selection (created area)
                        changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE);
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
        visualCommon.playerMoveSquare(moveType, area);
    }
}
