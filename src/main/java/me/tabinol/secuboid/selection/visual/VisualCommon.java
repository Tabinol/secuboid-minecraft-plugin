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
package me.tabinol.secuboid.selection.visual;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Visual selection common methods.
 */
class VisualCommon {

    private final Secuboid secuboid;
    private final VisualSelection visualSelection;
    private final Player player;
    private final PlayerConfEntry entry;
    private int y1;
    private int y2;
    private Location lastOutSideLoc = null; // for land retraction

    /**
     * Creates a visual selection common method for a new area.
     *
     * @param secuboid        the Secuboid instance
     * @param visualSelection thw visual selection instance
     * @param player          the player
     * @param entry           the player entry
     * @param playerLocation  the player location
     */
    VisualCommon(final Secuboid secuboid, final VisualSelection visualSelection, final Player player,
            final PlayerConfEntry entry, final Location playerLocation) {
        this.secuboid = secuboid;
        this.visualSelection = visualSelection;
        this.player = player;
        this.entry = entry;
        y1 = Integer.MAX_VALUE;
        y2 = Integer.MIN_VALUE;
        setBottomTop(playerLocation);
    }

    /**
     * Creates a visual selection common method for an existing area.
     *
     * @param visualSelection thw visual selection instance
     * @param player          the player
     * @param secuboid        the Secuboid instance
     * @param entry           the player entry
     * @param y1              the y1
     * @param y2              the y2
     */
    VisualCommon(final Secuboid secuboid, final VisualSelection visualSelection, final Player player,
            final PlayerConfEntry entry, final int y1, final int y2) {
        this.secuboid = secuboid;
        this.visualSelection = visualSelection;
        this.player = player;
        this.entry = entry;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Update top and bottom positions.
     *
     * @param playerLocation the player location
     */
    final void setBottomTop(final Location playerLocation) {
        final int playerBottom = entry.getSelectionBottom();
        final int maxBottom = secuboid.getConf().getMaxBottom();
        final int playerTop = entry.getSelectionTop();
        final int maxTop = secuboid.getConf().getMaxTop();
        final int playerY = playerLocation.getBlockY();
        if (playerBottom >= 0) {
            if (playerBottom != y1) {
                y1 = playerBottom < maxBottom ? maxBottom : playerBottom;
            }
        } else {
            final int newY1 = playerY + playerBottom;
            if (y1 < newY1) {
                y1 = newY1 < maxBottom ? maxBottom : newY1;
            }
        }
        if (playerTop >= 0) {
            if (playerTop != y2) {
                y2 = playerTop > maxTop ? maxTop : playerTop;
            }
        } else {
            final int newY2 = playerY - playerTop;
            if (y2 > newY2) {
                y2 = newY2 > maxTop ? maxTop : newY2;
            }
        }
    }

    /**
     * Gets the y1.
     *
     * @return the y1
     */
    int getY1() {
        return y1;
    }

    /**
     * Gets the y2.
     *
     * @return the y2
     */
    int getY2() {
        return y2;
    }

    /**
     * Gets the step X of visual selection to skip line (lag protect).
     *
     * @param area the area
     * @return the step x
     */
    int getStepX(final Area area) {
        final int result = ((area.getX2() - area.getX1()) / 32);
        return result > 1 ? result : 1;
    }

    /**
     * Gets the step Z of visual selection to skip line (lag protect).
     *
     * @param area the area
     * @return the step x
     */
    int getStepZ(final Area area) {
        final int result = ((area.getZ2() - area.getZ1()) / 32);
        return result > 1 ? result : 1;
    }

    /**
     * Colled when the player move (except for road).
     *
     * @param moveType the move type
     * @param area     the area
     */
    void playerMoveSquare(final AreaSelection.MoveType moveType, final Area area) {
        Location playerLoc;
        boolean isChanged = false;

        switch (moveType) {
        case MOVE:
            playerLoc = player.getLocation();
            setBottomTop(playerLoc);
            area.setY1(getY1());
            area.setY2(getY2());

            // Move with player
            if (playerLoc.getBlockX() - 1 < area.getX1()) {
                final int diffX = area.getX1() - playerLoc.getBlockX() + 1;
                area.setX1(area.getX1() - diffX);
                area.setX2(area.getX2() - diffX);
                isChanged = true;
            }
            if (playerLoc.getBlockX() + 1 > area.getX2()) {
                final int diffX = area.getX2() - playerLoc.getBlockX() - 1;
                area.setX1(area.getX1() - diffX);
                area.setX2(area.getX2() - diffX);
                isChanged = true;
            }
            if (playerLoc.getBlockZ() - 1 < area.getZ1()) {
                final int diffZ = area.getZ1() - playerLoc.getBlockZ() + 1;
                area.setZ1(area.getZ1() - diffZ);
                area.setZ2(area.getZ2() - diffZ);
                isChanged = true;
            }
            if (playerLoc.getBlockZ() + 1 > area.getZ2()) {
                final int diffZ = area.getZ2() - playerLoc.getBlockZ() - 1;
                area.setZ1(area.getZ1() - diffZ);
                area.setZ2(area.getZ2() - diffZ);
                isChanged = true;
            }
            break;

        case EXPAND:
            playerLoc = player.getLocation();
            setBottomTop(playerLoc);
            area.setY1(getY1());
            area.setY2(getY2());

            // Check where the player is outside the land
            if (playerLoc.getBlockX() - 1 < area.getX1()) {
                area.setX1(playerLoc.getBlockX() - 1);
                isChanged = true;
            }
            if (playerLoc.getBlockX() + 1 > area.getX2()) {
                area.setX2(playerLoc.getBlockX() + 1);
                isChanged = true;
            }
            if (playerLoc.getBlockZ() - 1 < area.getZ1()) {
                area.setZ1(playerLoc.getBlockZ() - 1);
                isChanged = true;
            }
            if (playerLoc.getBlockZ() + 1 > area.getZ2()) {
                area.setZ2(playerLoc.getBlockZ() + 1);
                isChanged = true;
            }
            break;

        case RETRACT:
            playerLoc = player.getLocation();
            final boolean isPlayerInside = area.isLocationInsideSquare(playerLoc.getBlockX(), playerLoc.getBlockZ());
            if (!isPlayerInside) {
                lastOutSideLoc = playerLoc;
            }
            setBottomTop(playerLoc);
            area.setY1(getY1());
            area.setY2(getY2());

            // Check where the player is inside the land
            if (isPlayerInside && lastOutSideLoc != null) {
                if (lastOutSideLoc.getBlockX() < area.getX1() && playerLoc.getBlockX() >= area.getX1()) {
                    area.setX1(playerLoc.getBlockX() + 1);
                    isChanged = true;
                } else if (lastOutSideLoc.getBlockX() > area.getX2() && playerLoc.getBlockX() <= area.getX2()) {
                    area.setX2(playerLoc.getBlockX() - 1);
                    isChanged = true;
                }
                if (lastOutSideLoc.getBlockZ() < area.getZ1() && playerLoc.getBlockZ() >= area.getZ1()) {
                    area.setZ1(playerLoc.getBlockZ() + 1);
                    isChanged = true;
                } else if (lastOutSideLoc.getBlockZ() > area.getZ2() && playerLoc.getBlockZ() <= area.getZ2()) {
                    area.setZ2(playerLoc.getBlockZ() - 1);
                    isChanged = true;
                }
            }
            // Negative size, put to player location
            if (area.getX1() > area.getX2()) {
                area.setX1(playerLoc.getBlockX());
                area.setX2(playerLoc.getBlockX());
            }
            if (area.getZ1() > area.getZ2()) {
                area.setZ1(playerLoc.getBlockZ());
                area.setZ2(playerLoc.getBlockZ());
            }
            break;

        default:
        }
        if (isChanged) {
            visualSelection.removeSelection();
            visualSelection.makeVisualSelection();
        }
    }
}
