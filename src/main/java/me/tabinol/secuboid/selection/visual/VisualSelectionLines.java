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
import me.tabinol.secuboid.lands.areas.LinesArea;
import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static java.lang.Math.abs;
import me.tabinol.secuboid.lands.DummyLand;
import me.tabinol.secuboid.lands.areas.Area;

public class VisualSelectionLines extends VisualSelection {

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

    public VisualSelectionLines(LinesArea area, boolean isFromLand, Player player,
                                int upDist, int downDist, int leftDist, int rightDist) {

        super(isFromLand, player);
        Location loc = player.getLocation();
        if (area != null) {
            this.area = area;
        } else {
            this.area = new LinesArea(loc.getWorld().getName(), null);
        }
        x1 = loc.getBlockX();
        y1 = loc.getBlockY();
        z1 = loc.getBlockZ();
        this.upDist = upDist;
        this.downDist = downDist;
        this.leftDist = leftDist;
        this.rightDist = rightDist;
    }

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
            DummyLand Land1 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
            DummyLand Land2 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX1(), area.getY1(), area.getZ2()));
            DummyLand Land3 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX2(), area.getY1(), area.getZ1()));
            DummyLand Land4 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX2(), area.getY1(), area.getZ2()));
            DummyLand Land5 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX1(), area.getY2(), area.getZ1()));
            DummyLand Land6 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX1(), area.getY2(), area.getZ2()));
            DummyLand Land7 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX2(), area.getY2(), area.getZ1()));
            DummyLand Land8 = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(new Location(
                    area.getWord(), area.getX2(), area.getY2(), area.getZ2()));

            if (Land1 == Land2 && Land1 == Land3 && Land1 == Land4 && Land1 == Land5 && Land1 == Land6
                    && Land1 == Land7 && Land1 == Land8) {
                parentDetected = Land1;
            } else {
                parentDetected = Secuboid.getThisPlugin().getLands().getOutsideArea(Land1.getWorldName());
            }

            canCreate = parentDetected.checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());
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
                        Location newloc = new Location(area.getWord(), posX, this.getYNearPlayer(posX, posZ) - 1, posZ);
                        Block block = newloc.getBlock();
                        blockList.put(newloc, block.getType());
                        blockByteList.put(newloc, block.getData());
                        DummyLand testCuboidarea = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(newloc);
                        if (parentDetected == testCuboidarea
                                && (canCreate || Secuboid.getThisPlugin().getPlayerConf().get(player).isAdminMod())) {
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
            Location newloc = new Location(area.getWord(), x1, this.getYNearPlayer(x1, z1) - 1, z1);
            Block block = newloc.getBlock();
            blockList.put(newloc, block.getType());
            blockByteList.put(newloc, block.getData());
            this.player.sendBlockChange(newloc, Material.BEACON, (byte) 0);
            newloc = new Location(area.getWord(), curLine.getX2(), this.getYNearPlayer(curLine.getX2(), curLine.getZ2()) - 1, curLine.getZ2());
            block = newloc.getBlock();
            blockList.put(newloc, block.getType());
            blockByteList.put(newloc, block.getData());
            this.player.sendBlockChange(newloc, Material.BEACON, (byte) 0);
        }
    }

    @Override
    public void playerMove(AreaSelection.MoveType moveType) {

        removeSelection();
        setActiveSelection();
    }
}
