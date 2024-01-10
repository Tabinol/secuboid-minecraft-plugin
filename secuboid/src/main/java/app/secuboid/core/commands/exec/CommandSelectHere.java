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
package app.secuboid.core.commands.exec;

import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.api.selection.PlayerSelection;
import app.secuboid.core.scoreboard.ScoreboardService;
import app.secuboid.core.selection.PlayerSelectionImpl;

// TODO Add player level from the land
@CommandRegistered(
        name = "select here"
)
public class CommandSelectHere implements CommandExec {

    private final ScoreboardService scoreboardService;

    public CommandSelectHere(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @Override
    public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
        // TODO select here
        PlayerInfo playerInfo = (PlayerInfo) commandSenderInfo;
        Area area = playerInfo.getArea();

        if (area == null) {
            // TODO Errot Not an area
            return;
        }

        // TODO Select permission

        PlayerSelection playerSelection = playerInfo.getPlayerSelection();
        ((PlayerSelectionImpl) playerSelection).createActiveSelectionAreaShow(scoreboardService, area);
    }
}
