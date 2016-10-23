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

import java.util.HashMap;
import java.util.Map;
import me.tabinol.secuboid.lands.DummyLand;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author michel
 */
public abstract class VisualSelection {
    
    /** The block list. */
    protected final Map<Location, Material> blockList;

    /** The block byte (option) list. */
    protected final Map<Location, Byte> blockByteList;
    
    /**
     *
     */
    protected final Player player;
    
    /** The is from land. */
    protected boolean isFromLand;
    
    /** The is collision. */
    protected boolean isCollision;
    
    /** Parent detected */
    protected DummyLand parentDetected;

    /**
     *
     * @param isFromLand
     * @param player
     */
    protected VisualSelection(boolean isFromLand, Player player) {
        
        blockList = new HashMap<Location, Material>();
        blockByteList = new HashMap<Location, Byte>();
        this.isFromLand = isFromLand;
        this.player = player;
        isCollision = false;
        parentDetected = null;
    }

    /**
     *
     * @return
     */
    public abstract Area getArea();

    /**
     * Sets the active selection.
     */
    public abstract void setActiveSelection();

    /**
     * Make visual selection.
     */
    public abstract void makeVisualSelection();
    
    // Called from AreaSelection then player listenner

    /**
     *
     * @param moveType
     */
    public abstract void playerMove(AreaSelection.MoveType moveType);
    
    /**
     * Gets the collision.
     *
     * @return the collision
     */
    public boolean getCollision() {
        
        return isCollision;
    }
    
    /**
     *
     */
    @SuppressWarnings("deprecation")
    public void removeSelection() {
        
        for (Map.Entry<Location, Material> entrySet : this.blockList.entrySet()) {
            this.player.sendBlockChange(entrySet.getKey(), entrySet.getValue(), blockByteList.get(entrySet.getKey()));
        }

        blockList.clear();
        blockByteList.clear();
    }

    /**
      * Gets the y near player before air.
      *
      * @param x the x
      * @param z the z
      * @return the y near player
      */
     protected int getYNearPlayer(int x, int z) {

        Location loc = new Location(player.getWorld(), x, player.getLocation().getY() - 1, z);

        if (!loc.getBlock().getType().isSolid()) {
            while (!loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
                    && loc.getBlockY() > 1) {
                loc.subtract(0, 1, 0);
            }
        } else {
            while (loc.getBlock().getType().isSolid() && loc.getBlockY() < player.getWorld().getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }

    /**
     *
     * @return
     */
    public Land getParentDetected() {
        
        if(parentDetected instanceof Land) {
            return (Land) parentDetected;
        } else {
            return null;
        }
    }

    /**
     * Create a new visual selection from default
     * @param areaType areaType
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(AreaType areaType, 
            boolean isFromLand, Player player) {
        
        if(areaType == AreaType.CUBOID) {
            return new VisualSelectionCuboid(null, isFromLand, player);
        }
        return new VisualSelectionCylinder(null, isFromLand, player);
    }

    /**
     * Create a visual selection from an area
     * @param area area
     * @param isFromLand is from land or must be false
     * @param player the player
     * @return visual selection
     */
    public static VisualSelection createVisualSelection(Area area, 
            boolean isFromLand, Player player) {
        
        if(area.getAreaType() == AreaType.CUBOID) {
            return new VisualSelectionCuboid((CuboidArea) area, 
                    isFromLand, player);
        }
        return new VisualSelectionCylinder((CylinderArea) area, 
                isFromLand, player);
    }
}
