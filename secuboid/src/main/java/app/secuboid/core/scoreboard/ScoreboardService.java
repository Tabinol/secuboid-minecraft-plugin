/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.core.scoreboard;

import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.services.Service;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardService implements Service {

    private static final String SCOREBOARD_PREFIX = "secuboid-";

    private final ScoreboardManager scoreboardManager;
    private final MessageManagerService messageManagerService;

    public ScoreboardService(ScoreboardManager scoreboardManager,
                             MessageManagerService messageManagerService) {
        this.scoreboardManager = scoreboardManager;
        this.messageManagerService = messageManagerService;
    }

    public SecuboidScoreboard create(Player player, String displayName, String... lines) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        String name = SCOREBOARD_PREFIX + player.getName();
        Objective objective = scoreboard.registerNewObjective(name, Criteria.DUMMY, displayName, RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int length = lines.length;

        for (int i = 0; i < length; i++) {
            Score score = objective.getScore(lines[i]);
            score.setScore(length - i);
        }

        player.setScoreboard(scoreboard);

        return new SecuboidScoreboard(player, scoreboard, objective, lines);
    }

    public String getMessage(MessageType messageType, MessagePath path) {
        return messageManagerService.get(messageType, path);
    }

    public void changeLine(SecuboidScoreboard secuboidScoreboard, int lineNb, String newLine) {
        Scoreboard scoreboard = secuboidScoreboard.getScoreboard();
        Objective objective = secuboidScoreboard.getObjective();
        String[] lines = secuboidScoreboard.getLines();

        scoreboard.resetScores(lines[lineNb]);
        Score score = objective.getScore(newLine);
        score.setScore(lines.length - lineNb);
        lines[lineNb] = newLine;
    }

    public void hide(SecuboidScoreboard secuboidScoreboard) {
        Player player = secuboidScoreboard.getPlayer();

        if (secuboidScoreboard.getPlayer().isOnline()) {
            player.setScoreboard(scoreboardManager.getMainScoreboard());
        }
    }
}
