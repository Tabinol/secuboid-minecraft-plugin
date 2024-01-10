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
import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.api.selection.PlayerSelection;
import app.secuboid.core.config.ConfigService;
import app.secuboid.core.scoreboard.ScoreboardService;
import app.secuboid.core.selection.PlayerSelectionImpl;
import lombok.RequiredArgsConstructor;

@CommandRegistered(
        name = "select cuboid",
        aliases = "cub",
        allowConsole = false,
        sourceActionFlags = "land-create"
)
@RequiredArgsConstructor
public class CommandSelectCuboid implements CommandExec {

    private final ConfigService configService;
    private final ScoreboardService scoreboardService;

    @Override
    public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
        PlayerInfo playerInfo = (PlayerInfo) commandSenderInfo;
        Land worldLand = playerInfo.getWorldLand();
        PlayerSelection playerSelection = playerInfo.getPlayerSelection();

        int startDiameter = configService.getSelectionDefaultStartDiameter();
        ((PlayerSelectionImpl) playerSelection).createActiveSelectionModifyExpand(scoreboardService, worldLand,
                AreaType.CUBOID, startDiameter);
    }
}
