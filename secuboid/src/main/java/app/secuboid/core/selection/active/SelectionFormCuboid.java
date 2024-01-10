/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.core.selection.active;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.core.utilities.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class SelectionFormCuboid extends SelectionForm {

    public SelectionFormCuboid(Area cuboidArea, Player player, boolean isResizeable, Land originLand, Area originArea) {
        super(cuboidArea, player, isResizeable, originLand, originArea);
    }

    @Override
    void refreshVisualSelection() {

        // Detect the current land from the 8 points
        // TODO No comments
        /*
         * LandPermissionsFlags landPermissionsFlags1 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX1(),
         * areaForm.getY1(), areaForm.getZ1()));
         * LandPermissionsFlags landPermissionsFlags2 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX1(),
         * areaForm.getY1(), areaForm.getZ2()));
         * LandPermissionsFlags landPermissionsFlags3 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX2(),
         * areaForm.getY1(), areaForm.getZ1()));
         * LandPermissionsFlags landPermissionsFlags4 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX2(),
         * areaForm.getY1(), areaForm.getZ2()));
         * LandPermissionsFlags landPermissionsFlags5 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX1(),
         * areaForm.getY2(), areaForm.getZ1()));
         * LandPermissionsFlags landPermissionsFlags6 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX1(),
         * areaForm.getY2(), areaForm.getZ2()));
         * LandPermissionsFlags landPermissionsFlags7 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX2(),
         * areaForm.getY2(), areaForm.getZ1()));
         * LandPermissionsFlags landPermissionsFlags8 = secuboid.getLands()
         * .getPermissionsFlags(new Location(areaForm.getWorld(), areaForm.getX2(),
         * areaForm.getY2(), areaForm.getZ2()));
         *
         * if (landPermissionsFlags1 == landPermissionsFlags2 && landPermissionsFlags1
         * == landPermissionsFlags3
         * && landPermissionsFlags1 == landPermissionsFlags4 && landPermissionsFlags1 ==
         * landPermissionsFlags5
         * && landPermissionsFlags1 == landPermissionsFlags6 && landPermissionsFlags1 ==
         * landPermissionsFlags7
         * && landPermissionsFlags1 == landPermissionsFlags8) {
         * parentFlagsDetected = landPermissionsFlags1;
         * } else {
         * parentFlagsDetected = secuboid.getLands()
         * .getOutsideLandPermissionsFlags(landPermissionsFlags1.getWorldNameNullable())
         * ;
         * }
         *
         * boolean canCreate = parentFlagsDetected.checkPermissionAndInherit(player,
         * PermissionList.LAND_CREATE.getPermissionType());
         */
        drawVisual();
    }

    private void drawVisual() {
        changedBlocks.resetBlocks();

        int stepX = getStepX();
        int stepZ = getStepZ();

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
                if (posX == area.getX1() || posX == area.getX2() || posZ == area.getZ1() ||
                        posZ == area.getZ2()) {

                    double posY = PlayerUtil.getYNearPlayer(player, posX, posZ) - 1d;
                    Location newLoc = new Location(world, posX, posY, posZ);

                    drawVisualRelative(newLoc);

                } else {
                    // Square center, skip!
                    posZ = area.getZ2() - 1;
                }
            }
        }
    }

    private void drawVisualRelative(Location newLoc) {
        // LandPermissionsFlags testCuboidarea =
        // secuboid.getLands().getPermissionsFlags(newLoc);
        // TODO Collision
        // if (parentFlagsDetected == testCuboidarea
        // && (canCreate || secuboid.getPlayerConf().get(player).isAdminMode())) {
        BlockData blockData;
        if (isResizeable) {
            blockData = MATERIAL_ACTIVE.createBlockData();
        } else {
            blockData = MATERIAL_AREA.createBlockData();
        }
        changedBlocks.changeBlock(newLoc, blockData);
        // } else {
        // changedBlocks.changeBlock(newLoc,
        // ChangedBlocks.SEL_COLLISION.createBlockData());
        // isCollision = true;
        // }
    }
}
