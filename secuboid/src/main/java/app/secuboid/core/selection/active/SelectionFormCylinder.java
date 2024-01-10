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
import org.bukkit.entity.Player;

public class SelectionFormCylinder extends SelectionForm {

    public SelectionFormCylinder(Area cylinderArea, Player player, boolean isResizeable, Land originLand, Area originArea) {
        super(cylinderArea, player, isResizeable, originLand, originArea);
    }

    @Override
    protected void refreshVisualSelection() {
        // TODO Auto-generated method stub
    }
}
