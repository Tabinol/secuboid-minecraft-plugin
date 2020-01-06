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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;

/**
 * The visual selection cuboid class.
 */
public final class VisualSelectionCuboid implements VisualSelection {

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
    private final boolean isActive;

    /**
     * The is collision.
     */
    private boolean isCollision;

    /**
     * Parent detected
     */
    private LandPermissionsFlags parentPermsFlagsDetected;

    private CuboidArea area;

    private final CuboidArea originalArea;

    public VisualSelectionCuboid(final Secuboid secuboid, final CuboidArea area, final CuboidArea originalArea,
            final boolean isActive, final Player player) {
        this.secuboid = secuboid;
        this.originalArea = originalArea;
        if (area == null) {
            visualCommon = new VisualCommon(secuboid, this, player, secuboid.getPlayerConf().get(player),
                    player.getLocation());
        } else {
            visualCommon = new VisualCommon(secuboid, this, player, secuboid.getPlayerConf().get(player), area.getY1(),
                    area.getY2());
        }
        changedBlocks = new ChangedBlocks(player);
        this.isActive = isActive;
        this.player = player;
        isCollision = false;
        parentPermsFlagsDetected = null;
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
    public LandPermissionsFlags getParentPermsFlagsDetected() {
        return parentPermsFlagsDetected;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public Area getOriginalArea() {
        return originalArea;
    }

    @Override
    public void setActiveSelection() {

        isCollision = false;

        final Location loc = player.getLocation();
        final int landXr = secuboid.getConf().getDefaultXSize() / 2;
        final int landZr = secuboid.getConf().getDefaultZSize() / 2;
        area = new CuboidArea(false, loc.getWorld().getName(), loc.getBlockX() - landXr, visualCommon.getY1(),
                loc.getBlockZ() - landZr, loc.getBlockX() + landXr, visualCommon.getY2(), loc.getBlockZ() + landZr);

        makeVisualSelection();
    }

    @Override
    public void makeVisualSelection() {

        // Detect the current land from the 8 points
        final LandPermissionsFlags landPermissionsFlags1 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
        final LandPermissionsFlags landPermissionsFlags2 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX1(), area.getY1(), area.getZ2()));
        final LandPermissionsFlags landPermissionsFlags3 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX2(), area.getY1(), area.getZ1()));
        final LandPermissionsFlags landPermissionsFlags4 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX2(), area.getY1(), area.getZ2()));
        final LandPermissionsFlags landPermissionsFlags5 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX1(), area.getY2(), area.getZ1()));
        final LandPermissionsFlags landPermissionsFlags6 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX1(), area.getY2(), area.getZ2()));
        final LandPermissionsFlags landPermissionsFlags7 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX2(), area.getY2(), area.getZ1()));
        final LandPermissionsFlags landPermissionsFlags8 = secuboid.getLands()
                .getPermissionsFlags(new Location(area.getWord(), area.getX2(), area.getY2(), area.getZ2()));

        if (landPermissionsFlags1 == landPermissionsFlags2 && landPermissionsFlags1 == landPermissionsFlags3
                && landPermissionsFlags1 == landPermissionsFlags4 && landPermissionsFlags1 == landPermissionsFlags5
                && landPermissionsFlags1 == landPermissionsFlags6 && landPermissionsFlags1 == landPermissionsFlags7
                && landPermissionsFlags1 == landPermissionsFlags8) {
            parentPermsFlagsDetected = landPermissionsFlags1;
        } else {
            parentPermsFlagsDetected = secuboid.getLands()
                    .getOutsideLandPermissionsFlags(landPermissionsFlags1.getWorldNameNullable());
        }

        final boolean canCreate = parentPermsFlagsDetected.checkPermissionAndInherit(player,
                PermissionList.LAND_CREATE.getPermissionType());

        // MakeSquare
        final int stepX = visualCommon.getStepX(area);
        final int stepZ = visualCommon.getStepZ(area);

        int posX = area.getX1() - stepX;
        while (posX < area.getX2()) {
            posX += stepX;
            // Force to do not skip the X line
            if (posX > area.getX2()) {
                posX = area.getX2();
            }
            int posZ = area.getZ1() - stepZ;
            while (posZ < area.getZ2()) {
                posZ += stepZ;
                // Force to do not skip the Z line
                if (posZ > area.getZ2()) {
                    posZ = area.getZ2();
                }
                if (posX == area.getX1() || posX == area.getX2() || posZ == area.getZ1() || posZ == area.getZ2()) {

                    final Location newloc = new Location(area.getWord(), posX,
                            PlayersUtil.getYNearPlayer(player, posX, posZ) - 1d, posZ);

                    if (isActive) {
                        // Active Selection
                        final LandPermissionsFlags testCuboidarea = secuboid.getLands().getPermissionsFlags(newloc);
                        if (parentPermsFlagsDetected == testCuboidarea
                                && (canCreate || secuboid.getPlayerConf().get(player).isAdminMode())) {
                            changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_ACTIVE.createBlockData());
                        } else {
                            changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_COLLISION.createBlockData());
                            isCollision = true;
                        }
                    } else {
                        // Passive Selection (created area)
                        changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE.createBlockData());
                    }

                } else {
                    // Square center, skip!
                    posZ = area.getZ2() - 1;
                }
            }
        }
    }

    @Override
    public void playerMove(final AreaSelection.MoveType moveType) {
        visualCommon.playerMoveSquare(moveType, area);
    }
}
