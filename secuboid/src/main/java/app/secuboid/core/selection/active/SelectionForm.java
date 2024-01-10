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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class SelectionForm {

    protected static final Material MATERIAL_ACTIVE = Material.SPONGE;
    protected static final Material MATERIAL_COLLISION = Material.REDSTONE_BLOCK;
    protected static final Material MATERIAL_AREA = Material.IRON_BLOCK;

    protected final Area area;
    protected final Player player;
    protected final boolean isResizeable;
    protected final Land originLand;
    protected final Area originArea;
    protected final World world;

    protected ChangedBlocks changedBlocks;
    protected boolean hasCollision;

    SelectionForm(Area area, Player player, boolean isResizeable, Land originLand, Area originArea) {
        this.area = area;
        this.player = player;
        this.isResizeable = isResizeable;
        this.originLand = originLand;
        this.originArea = originArea;
        world = player.getWorld();

        changedBlocks = new ChangedBlocks(player);
        hasCollision = false;
    }

    public final Area getArea() {
        return area;
    }

    public final Player getPlayer() {
        return player;
    }

    public final boolean isResizeable() {
        return isResizeable;
    }

    public final Land getOriginLand() {
        return originLand;
    }

    public final Area getOriginArea() {
        return originArea;
    }

    public final boolean hasCollision() {
        return hasCollision;
    }

    public final void removeSelection() {
        changedBlocks.resetBlocks();
    }

    abstract void refreshVisualSelection();

    protected int getStepX() {
        int result = ((area.getX2() - area.getX1()) / 32);
        return Math.max(result, 1);
    }

    protected int getStepZ() {
        int result = ((area.getZ2() - area.getZ1()) / 32);
        return Math.max(result, 1);
    }
}
