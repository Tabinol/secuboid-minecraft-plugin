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
import org.bukkit.entity.Player;

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

        //MakeSquare
        for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
                if (posX == area.getX1() || posX == area.getX2()
                        || posZ == area.getZ1() || posZ == area.getZ2()) {

                    Location newloc = new Location(area.getWord(), posX, PlayersUtil.getYNearPlayer(player, posX, posZ) - 1, posZ);

                    if (!isFromLand) {

                        // Active Selection
                        Land testCuboidarea = secuboid.getLands().getLandOrOutsideArea(newloc);
                        if (outsideArea == testCuboidarea
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

        switch (moveType) {
            case ACTIVE:

                removeSelection();
                Location playerLoc = player.getLocation();

                // Check where the player is outside the land
               //if (playerLoc.getBlockX() - 1 < area.getX1()) {
               //     area.setX1(playerLoc.getBlockX() - 1);
                //}
                //if (playerLoc.getBlockX() + 1 > area.getX2()) {
                //    area.setX2(playerLoc.getBlockX() + 1);
                //}
                //if (playerLoc.getBlockZ() - 1 < area.getZ1()) {
                //    area.setZ1(playerLoc.getBlockZ() - 1);
                //}
                //if (playerLoc.getBlockZ() + 1 > area.getZ2()) {
                //    area.setZ2(playerLoc.getBlockZ() + 1);
                //}

                makeVisualSelection();
                break;

            default:
        }
    }
}
