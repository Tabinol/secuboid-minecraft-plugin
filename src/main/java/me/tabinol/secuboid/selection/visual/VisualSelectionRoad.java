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
import me.tabinol.secuboid.lands.GlobalLand;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.PlayersUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.EnumSet;

/**
 * The visual selection cuboid class. Parent is not detected.
 */
public class VisualSelectionRoad implements VisualSelection {

    private final Secuboid secuboid;

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

    private RoadArea area;

    public VisualSelectionRoad(Secuboid secuboid, RoadArea area, boolean isFromLand, Player player) {
        this.secuboid = secuboid;
        changedBlocks = new ChangedBlocks(player);
        this.isFromLand = isFromLand;
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
    public RealLand getParentDetected() {
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
        area = new RoadArea(loc.getWorld().getName(), secuboid.getConf().getDefaultBottom(),
                secuboid.getConf().getDefaultTop(), null);

        makeVisualSelection();
    }

    @Override
    public void makeVisualSelection() {

        GlobalLand outsideArea = secuboid.getLands().getOutsideArea(area.getWorldName());
        boolean canCreate = outsideArea.getPermissionsFlags()
                .checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

        // Makes borders X
        boolean isZ = false;
        for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
            boolean isLastActive = false;
            if (!isFromLand || posX % 5 == 0) {
                for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
                    isLastActive = makeBorders(isZ, posX, posZ, isLastActive, outsideArea, canCreate);
                }
            }
        }

        // Makes borders Z
        isZ = true;
        for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
            boolean isLastActive = false;
            if (!isFromLand || posZ % 5 == 0) {
                for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
                    isLastActive = makeBorders(isZ, posX, posZ, isLastActive, outsideArea, canCreate);
                }
            }
        }
    }

    private boolean makeBorders(boolean isZ, int posX, int posZ, boolean isLastActive, GlobalLand outsideArea, boolean canCreate) {

        Location newloc = new Location(area.getWord(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1, posZ);
        boolean isLocationInside = area.isLocationInside(newloc);
        if (isLastActive) {
            if (!isLocationInside) {
                Location lastLoc;
                if (isZ) {
                    lastLoc = newloc.add(1, 0, 0);
                } else {
                    lastLoc = newloc.add(0, 0, 1);
                }

                // Not active selection
                setChangedBlocks(outsideArea, canCreate, lastLoc);
                return false;
            }
        } else {
            if (isLocationInside) {

                // Active Selection
                setChangedBlocks(outsideArea, canCreate, newloc);
                return true;
            }
        }
        return isLastActive;
    }

    private void setChangedBlocks(GlobalLand outsideArea, boolean canCreate, Location newloc) {

        Land testArea = secuboid.getLands().getLandOrOutsideArea(newloc);
        if(isFromLand) {
            changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_PASSIVE_SUBCORNER);
        } else {
            if (outsideArea == testArea && (canCreate || secuboid.getPlayerConf().get(player).isAdminMode())) {
                changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_ACTIVE);
            } else {
                changedBlocks.changeBlock(newloc, ChangedBlocks.SEL_COLLISION);
                isCollision = true;
            }
        }
    }

    @Override
    public void playerMove(AreaSelection.MoveType moveType) {
        switch (moveType) {
            case ACTIVE:

                removeSelection();
                Location playerLoc = player.getLocation();
                boolean active;
                int posX = playerLoc.getBlockX();
                int posZ = playerLoc.getBlockZ();
                int radius = secuboid.getConf().getDefaultRadius();

                // Detect selection
                for(int x = posX; x >= posX - radius; posX --) {
                    active = true;
                    for(int z = posZ; z >= posZ - radius; posZ --) {
                        active = checkForPoint(active, x, z);
                    }
                    active = true;
                    for(int z = posZ; z <= posZ + radius; posZ ++) {
                        active = checkForPoint(active, x, z);
                    }
                }
                for(int x = posX; x <= posX + radius; posX ++) {
                    active = true;
                    for(int z = posZ; z >= posZ - radius; posZ --) {
                        active = checkForPoint(active, x, z);
                    }
                    active = true;
                    for(int z = posZ; z <= posZ + radius; posZ ++) {
                        active = checkForPoint(active, x, z);
                    }
                }
                for(int z = posZ; z >= posZ - radius; posZ --) {
                    active = true;
                    for(int x = posZ; x >= posX - radius; posX --) {
                        active = checkForPoint(active, x, z);
                    }
                    active = true;
                    for(int x = posX; z <= posX + radius; posX ++) {
                        active = checkForPoint(active, x, z);
                    }
                }
                for(int z = posZ; z <= posZ + radius; posZ ++) {
                    active = true;
                    for(int x = posZ; x >= posX - radius; posX --) {
                        active = checkForPoint(active, x, z);
                    }
                    active = true;
                    for(int x = posX; z <= posX + radius; posX ++) {
                        active = checkForPoint(active, x, z);
                    }
                }

                makeVisualSelection();
                break;

            default:
        }
    }

    private boolean checkForPoint(boolean active, int x, int z) {

        EnumSet<Material> nonSelectedMaterials = secuboid.getConf().getDefaultNonSelectedMaterials();
        Location newloc = new Location(area.getWord(), x, PlayersUtil.getYNearPlayer(player, x, z) - 1, z);

        if(!active || nonSelectedMaterials.contains(newloc.getBlock().getType())) {
            return false;
        }
        area.add(x, z);

        return true;
    }
}
