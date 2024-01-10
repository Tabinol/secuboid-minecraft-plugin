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
import app.secuboid.api.messages.MessagePath;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.selection.active.ActiveSelectionModify;
import app.secuboid.core.lands.areas.AreaImpl;
import app.secuboid.core.messages.MessagePaths;
import app.secuboid.core.scoreboard.ScoreboardService;
import org.bukkit.entity.Player;

import java.util.Map;

import static app.secuboid.core.messages.Log.log;
import static java.util.logging.Level.WARNING;

class SelectionScoreboardActive extends SelectionScoreboard {

    private static final String MESSAGE_PATH_MOVE_TYPE_PREFIX = "selection.scoreboard.active.selection-types.";
    private static final String COMMAND_CREATE = "/sd create";
    private static final Map<Class<? extends ActiveSelectionModify>, String> CLASS_TO_MESSAGE_TAG = Map.of(
            ActiveSelectionModifyExpand.class, "expand",
            ActiveSelectionModifyMove.class, "move",
            ActiveSelectionModifyPassive.class, "passive",
            ActiveSelectionModifyRetract.class, "retract");

    private final Area area;
    private final Class<? extends ActiveSelectionModify> activeSelectionModifyClass;

    SelectionScoreboardActive(ScoreboardService scoreboardService, Player player,
                              Area area, Class<?
            extends ActiveSelectionModify> activeSelectionModifyClass) {
        super(scoreboardService, player);
        this.area = area;
        this.activeSelectionModifyClass = activeSelectionModifyClass;
    }

    @Override
    void init() {
        String title = scoreboardService.getMessage(MessageType.TITLE, MessagePaths.selectionScoreboardActiveTitleCreate());
        String selectionTypeMsg = getSelectionTypeMsg(activeSelectionModifyClass);
        String[] lines = new String[5];
        lines[0] = scoreboardService.getMessage(MessageType.NORMAL,
                MessagePaths.selectionScoreboardActiveSelectionType(selectionTypeMsg));
        lines[1] = scoreboardService.getMessage(MessageType.NORMAL, ((AreaImpl) area).getMessagePath());
        long volume = area.getVolume();
        lines[2] = scoreboardService.getMessage(MessageType.NORMAL, MessagePaths.selectionScoreboardActiveVolume(volume));
        lines[3] = "";
        lines[4] = scoreboardService.getMessage(MessageType.NORMAL, MessagePaths.selectionScoreboardActiveTypeWhenDone(COMMAND_CREATE));

        scoreboard = scoreboardService.create(player, title, lines);
    }

    @Override
    void update() {
        if (scoreboard == null) {
            log().log(WARNING, "No scoreboard to update for the player: {}", player.getName());
            return;
        }

        long volume = area.getVolume();
        String line1 = scoreboardService.getMessage(MessageType.NORMAL, ((AreaImpl) area).getMessagePath());
        scoreboardService.changeLine(scoreboard, 1, line1);
        String line2 = scoreboardService.getMessage(MessageType.NORMAL, MessagePaths.selectionScoreboardActiveVolume(volume));
        scoreboardService.changeLine(scoreboard, 2, line2);
    }

    private String getSelectionTypeMsg(Class<? extends ActiveSelectionModify> activeSelectionModifyClass) {
        String msgTag = CLASS_TO_MESSAGE_TAG.get(activeSelectionModifyClass);

        String path = MESSAGE_PATH_MOVE_TYPE_PREFIX + msgTag;
        MessagePath messagePath = MessagePath.newInstance(path, new String[]{}, new Object[]{});
        return scoreboardService.getMessage(MessageType.NO_COLOR, messagePath);
    }
}
