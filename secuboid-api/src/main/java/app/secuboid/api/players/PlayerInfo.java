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
package app.secuboid.api.players;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.selection.PlayerSelection;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Contains information for a specific player.
 */
public interface PlayerInfo extends CommandSenderInfo {

    /**
     * Gets the Bukkit player instance.
     *
     * @return the player (or null for the console)
     */
    Player getPlayer();

    /**
     * Gets the player UUID.
     *
     * @return the player UUID
     */
    UUID getUUID();

    /**
     * Sets the player in Admin Mode
     *
     * @param value true for Admin Mode
     */
    void setAdminMode(boolean value);

    /**
     * Gets the Area where the player is.
     *
     * @return the last area or null if the player is outside an area
     */
    Area getArea();

    /**
     * Gets the land where the player is.
     *
     * @return the last land
     */
    Land getLand();

    /**
     * Gets the location path (area or world land) where the player is.
     *
     * @return the last location path (area or world land)
     */
    LocationPath getLocationPath();

    /**
     * Gets the world land where the player is.
     *
     * @return the last world land
     */
    Land getWorldLand();

    /**
     * Gets the player selection.
     *
     * @return the player selection
     */
    PlayerSelection getPlayerSelection();
}
