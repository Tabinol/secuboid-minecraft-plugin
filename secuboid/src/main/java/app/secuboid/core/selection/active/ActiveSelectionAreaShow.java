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

import app.secuboid.api.lands.areas.Area;
import app.secuboid.core.scoreboard.ScoreboardService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActiveSelectionAreaShow extends ActiveSelectionAreaImpl {

    private final Player player;
    private final SelectionForm selectionForm;

    private SelectionScoreboard selectionScoreboard;
    private Location playerLastLoc;

    public ActiveSelectionAreaShow(Player player, Area area, SelectionForm selectionForm) {
        super(player, area);
        this.player = player;
        this.selectionForm = selectionForm;
        selectionScoreboard = null;
        playerLastLoc = null;
    }

    @Override
    public void init(ScoreboardService scoreboardService) {
        playerLastLoc = player.getLocation();
        selectionForm.refreshVisualSelection();
        selectionScoreboard = new SelectionScoreboardArea(scoreboardService, player, area);
        selectionScoreboard.init();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void playerMoveSelection() {
        Location playerLoc = player.getLocation();

        if (playerLastLoc != null && playerLoc.distanceSquared(playerLastLoc) >= 4D) {
            playerLastLoc = playerLoc;
            selectionForm.refreshVisualSelection();
        }
    }

    @Override
    public final void removeSelection() {
        selectionForm.removeSelection();

        if (selectionScoreboard != null) {
            selectionScoreboard.hide();
        }
    }
}
