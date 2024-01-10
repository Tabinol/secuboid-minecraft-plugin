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
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.core.persistence.jpa.AreaJPA;
import org.bukkit.Location;

public class ActiveSelectionModifyExpand extends ActiveSelectionModifyImpl {

    public ActiveSelectionModifyExpand(Land worldLand, PlayerInfo playerInfo, SelectionForm selectionForm) {
        super(worldLand, playerInfo, selectionForm);
    }

    @Override
    protected boolean playerMoveSelectionCheckChanged(Location playerLoc) {
        Area area = selectionForm.getArea();
        boolean isChanged = false;

        // Check where the player is outside the land
        if (playerLoc.getBlockX() - 1 < area.getX1()) {
            ((AreaJPA) area).setX1(playerLoc.getBlockX() - 1);
            isChanged = true;
        }
        if (playerLoc.getBlockX() + 1 > area.getX2()) {
            ((AreaJPA) area).setX2(playerLoc.getBlockX() + 1);
            isChanged = true;
        }
        if (playerLoc.getBlockZ() - 1 < area.getZ1()) {
            ((AreaJPA) area).setZ1(playerLoc.getBlockZ() - 1);
            isChanged = true;
        }
        if (playerLoc.getBlockZ() + 1 > area.getZ2()) {
            ((AreaJPA) area).setZ2(playerLoc.getBlockZ() + 1);
            isChanged = true;
        }

        return isChanged;
    }
}
