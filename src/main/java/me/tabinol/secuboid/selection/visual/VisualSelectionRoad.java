/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;

/**
 * The visual selection cuboid class. Parent is not detected.
 */
public final class VisualSelectionRoad implements VisualSelection {

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

    private RoadArea area;

    private final RoadArea originalArea;

    /**
     * True if there is a change and we have to change visual selection;
     */
    private boolean isAreaChange = false;

    public VisualSelectionRoad(final Secuboid secuboid, final RoadArea area, final RoadArea originalArea,
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
        return secuboid.getLands().getOutsideLandPermissionsFlags(area.getWorldName());
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
        area = new RoadArea(false, loc.getWorld().getName(), visualCommon.getY1(), visualCommon.getY2(), null);
        moveWithPlayer(true);
    }

    @Override
    public void makeVisualSelection() {

        final LandPermissionsFlags outsidePermsFlags = secuboid.getLands()
                .getOutsideLandPermissionsFlags(area.getWorldName());
        final boolean canCreate = outsidePermsFlags.checkPermissionAndInherit(player,
                PermissionList.LAND_CREATE.getPermissionType());
        final int stepX = visualCommon.getStepX(area);
        final int stepZ = visualCommon.getStepZ(area);

        // Makes borders X
        for (int posX = area.getX1(); posX <= area.getX2(); posX += stepX) {
            boolean isLastActive = false;
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ += stepZ) {
                isLastActive = makeBorders(false, posX, posZ, isLastActive, outsidePermsFlags, canCreate);
            }
        }

        // Makes borders Z
        for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ += stepZ) {
            boolean isLastActive = false;
            for (int posX = area.getX1(); posX <= area.getX2(); posX += stepX) {
                isLastActive = makeBorders(true, posX, posZ, isLastActive, outsidePermsFlags, canCreate);
            }
        }
    }

    private boolean makeBorders(final boolean isZ, final int posX, final int posZ, final boolean isLastActive,
            final LandPermissionsFlags outsidePermsFlags, final boolean canCreate) {

        final Location newloc = new Location(area.getWorld(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1d,
                posZ);
        final boolean isLocationInside = area.isLocationInside(newloc);
        if (isLastActive) {
            if (!isLocationInside) {
                Location lastLoc;
                if (isZ) {
                    lastLoc = newloc.subtract(1, 0, 0);
                } else {
                    lastLoc = newloc.subtract(0, 0, 1);
                }

                // Not active selection
                lastLoc.setY(PlayersUtil.getYNearPlayer(player, lastLoc.getBlockX(), lastLoc.getBlockZ()) - 1);
                setChangedBlocks(outsidePermsFlags, canCreate, lastLoc);
                return false;
            }
        } else {
            if (isLocationInside) {

                // Active Selection
                setChangedBlocks(outsidePermsFlags, canCreate, newloc);
                return true;
            }
        }
        return isLastActive;
    }

    private void setChangedBlocks(final LandPermissionsFlags outsidePermsFlags, final boolean canCreate,
            final Location newloc) {

        final LandPermissionsFlags testPermissionsFlags = secuboid.getLands().getPermissionsFlags(newloc);
        if (!isActive) {
            changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE.createBlockData());
        } else {
            if (outsidePermsFlags == testPermissionsFlags
                    && (canCreate || secuboid.getPlayerConf().get(player).isAdminMode())) {
                changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_ACTIVE.createBlockData());
            } else {
                changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_COLLISION.createBlockData());
                isCollision = true;
            }
        }
    }

    @Override
    public void playerMove(final AreaSelection.MoveType moveType) {
        switch (moveType) {
        case EXPAND:
            moveWithPlayer(true);
            break;

        case RETRACT:
            moveWithPlayer(false);
            break;

        default:
        }
    }

    private void moveWithPlayer(final boolean isAdd) {

        isAreaChange = false;
        final Location playerLoc = player.getLocation();
        visualCommon.setBottomTop(playerLoc);
        final int posX = playerLoc.getBlockX();
        final int posZ = playerLoc.getBlockZ();
        int radius = secuboid.getPlayerConf().get(player).getSelectionRadius();
        final int maxRadius = secuboid.getConf().getMaxRadius();
        if (radius > maxRadius) {
            radius = maxRadius;
        }
        area.setY1(visualCommon.getY1());
        area.setY2(visualCommon.getY2());

        // Detect selection
        for (int x = posX; x >= posX - radius; x--) {
            checkZPoints(x, posZ, radius, isAdd);
        }
        for (int x = posX; x <= posX + radius; x++) {
            checkZPoints(x, posZ, radius, isAdd);
        }
        for (int z = posZ; z >= posZ - radius; z--) {
            checkXPoints(z, posX, radius, isAdd);
        }
        for (int z = posZ; z <= posZ + radius; z++) {
            checkXPoints(z, posX, radius, isAdd);
        }
        if (isAreaChange) {
            removeSelection();
            makeVisualSelection();
        }
    }

    private void checkZPoints(final int x, final int posZ, final int radius, final boolean isAdd) {

        boolean active;

        active = true;
        for (int z = posZ; active && z >= posZ - radius; z--) {
            active = checkForPoint(isAdd, x, z);
        }
        active = true;
        for (int z = posZ; active && z <= posZ + radius; z++) {
            active = checkForPoint(isAdd, x, z);
        }
    }

    private void checkXPoints(final int z, final int posX, final int radius, final boolean isAdd) {

        boolean active;

        active = true;
        for (int x = posX; active && x >= posX - radius; x--) {
            active = checkForPoint(isAdd, x, z);
        }
        active = true;
        for (int x = posX; active && x <= posX + radius; x++) {
            active = checkForPoint(isAdd, x, z);
        }
    }

    private boolean checkForPoint(final boolean isAdd, final int x, final int z) {
        final EnumSet<Material> nonSelectedMaterials = secuboid.getConf().getDefaultNonSelectedMaterials();
        final Location newloc = new Location(area.getWorld(), x, PlayersUtil.getYNearPlayer(player, x, z) - 1d, z);

        if (isAdd) {
            // Add point
            if (nonSelectedMaterials.contains(newloc.getBlock().getType())) {
                return false;
            }
            if (!area.getPoint(x, z)) {
                area.add(x, z);
                isAreaChange = true;
            }
        } else {

            // Erase
            area.remove(x, z);
        }

        return true;
    }
}
