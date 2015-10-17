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
package me.tabinol.secuboid.selection.region;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;

import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class ActiveAreaSelection.
 */
public class ActiveAreaSelection extends AreaSelection implements PlayerMoveListen {

    /**
     * Instantiates a new active area selection.
     *
     * @param player the player
     */
    public ActiveAreaSelection(Player player) {

        super(player);
        setActiveSelection();
    }

    /**
     * Instantiates a new active area selection.
     *
     * @param player the player
     * @param area the area
     */
    public ActiveAreaSelection(Player player, ICuboidArea area) {

        super(player);
        this.area = area;
        makeVisualSelection();
    }

    /**
     * Sets the active selection.
     */
    public final void setActiveSelection() {

        isCollision = false;

        Location loc = player.getLocation();
        int landXr = Secuboid.getThisPlugin().iConf().getDefaultXSize() / 2;
        int landZr = Secuboid.getThisPlugin().iConf().getDefaultZSize() / 2;
        area = new CuboidArea(loc.getWorld().getName(),
                loc.getBlockX() - landXr, Secuboid.getThisPlugin().iConf().getDefaultBottom(), loc.getBlockZ() - landZr,
                loc.getBlockX() + landXr, Secuboid.getThisPlugin().iConf().getDefaultTop(), loc.getBlockZ() + landZr);

        makeVisualSelection();
    }

    // Called from player listenner
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.selection.region.PlayerMoveListen#playerMove()
     */
    @Override
    public void playerMove() {

        removeSelection();
        setActiveSelection();
    }
}
