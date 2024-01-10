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
package app.secuboid.core.selection;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.selection.PlayerSelection;
import app.secuboid.api.selection.active.ActiveSelection;
import app.secuboid.core.lands.areas.AreaImpl;
import app.secuboid.core.persistence.jpa.AreaJPA;
import app.secuboid.core.scoreboard.ScoreboardService;
import app.secuboid.core.selection.active.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class PlayerSelectionImpl extends SenderSelectionImpl implements PlayerSelection {

    private final PlayerInfo playerInfo;
    private final Player player;

    public PlayerSelectionImpl(PlayerInfo playerInfo) {
        super(playerInfo.getPlayer());
        this.playerInfo = playerInfo;
        this.player = playerInfo.getPlayer();
    }

    public void createActiveSelectionModifyExpand(ScoreboardService scoreboardService, Land worldLand,
                                                  AreaType areaType, int startDiameter) {
        Area area = createAreaForm(areaType, startDiameter);
        createActiveSelectionModifyExpand(scoreboardService, worldLand, area);
    }

    public void createActiveSelectionModifyExpand(ScoreboardService scoreboardService, Land worldLand, Area area) {
        createActiveSelection(scoreboardService, area, true, s -> new ActiveSelectionModifyExpand(worldLand,
                playerInfo, s));
    }

    public void createActiveSelectionAreaShow(ScoreboardService scoreboardService, Area area) {
        createActiveSelection(scoreboardService, area, false, s -> new ActiveSelectionAreaShow(player, area, s));
    }

    public void updateSelectionFromLocation() {
        if (hasSelection()) {
            ((ActiveSelectionImpl) activeSelection).playerMoveSelection();
        }
    }

    private Area createAreaForm(AreaType areaType, int startDiameter) {
        Location loc = player.getLocation();
        int playerX = loc.getBlockX();
        int playerZ = loc.getBlockZ();

        // TODO Change to customizable limit y1/y2
        World world = player.getWorld();
        int y1 = world.getMinHeight();
        int y2 = world.getMaxHeight();
        int x1 = playerX - (startDiameter / 2);
        int x2 = x1 + startDiameter;
        int z1 = playerZ - (startDiameter / 2);
        int z2 = z1 + startDiameter;

        AreaJPA areaJPA = AreaJPA.builder()
                .type(areaType)
                .x1(x1)
                .y1(y1)
                .z1(z1)
                .x2(x2)
                .y2(y2)
                .z2(z2)
                .build();

        return AreaImpl.newInstance(areaType, areaJPA, null);
    }

    private void createActiveSelection(ScoreboardService scoreboardService, Area area, boolean isResizeable,
                                       Function<SelectionForm, ActiveSelection> selectionFormActiveSelectionFunction) {
        SelectionForm selectionForm = createSelectionForm(area, isResizeable);
        activeSelection = selectionFormActiveSelectionFunction.apply(selectionForm);
        ((ActiveSelectionImpl) activeSelection).init(scoreboardService);
    }

    private SelectionForm createSelectionForm(Area area, boolean isResizeable) {
        return switch (area.getType()) {
            case CUBOID -> new SelectionFormCuboid(area, player, isResizeable, null, null);
            case CYLINDER -> new SelectionFormCylinder(area, player, isResizeable, null, null);
        };
    }

}