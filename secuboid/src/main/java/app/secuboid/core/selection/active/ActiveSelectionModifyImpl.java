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
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.selection.active.ActiveSelectionModify;
import app.secuboid.core.scoreboard.ScoreboardService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ActiveSelectionModifyImpl extends ActiveSelectionImpl implements ActiveSelectionModify {

    protected final Land worldLand;
    protected final PlayerInfo playerInfo;
    protected final Player player;
    protected final SelectionForm selectionForm;
    private SelectionScoreboard selectionScoreboard;
    private Location playerLastLoc;

    protected ActiveSelectionModifyImpl(Land worldLand, PlayerInfo playerInfo,
                                        SelectionForm selectionForm) {
        super(playerInfo.getPlayer());
        this.worldLand = worldLand;
        this.playerInfo = playerInfo;
        player = playerInfo.getPlayer();
        this.selectionForm = selectionForm;

        selectionScoreboard = null;
        playerLastLoc = null;
    }

    @Override
    public void init(ScoreboardService scoreboardService) {
        playerLastLoc = player.getLocation();
        selectionForm.refreshVisualSelection();
        selectionScoreboard = new SelectionScoreboardActive(scoreboardService, player, selectionForm.area,
                this.getClass());
        selectionScoreboard.init();
    }

    public SelectionForm getSelectionForm() {
        return selectionForm;
    }

    @Override
    public final void playerMoveSelection() {
        Location playerLoc = player.getLocation();
        boolean isChanged = playerMoveSelectionCheckChanged(playerLoc);

        if (!isChanged && playerLastLoc != null && playerLoc.distanceSquared(playerLastLoc) >= 4D) {
            isChanged = true;
        }

        if (isChanged) {
            playerLastLoc = playerLoc;
            selectionForm.refreshVisualSelection();


            if (!(this instanceof ActiveSelectionModifyPassive) && selectionScoreboard != null) {
                selectionScoreboard.update();
            }
        }
    }

    @Override
    public final void removeSelection() {
        selectionForm.removeSelection();

        if (selectionScoreboard != null) {
            selectionScoreboard.hide();
        }
    }

    @Override
    public Land getWorldLand() {
        return worldLand;
    }

    protected abstract boolean playerMoveSelectionCheckChanged(Location playerLoc);
}
