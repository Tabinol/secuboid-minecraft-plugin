/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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
package me.tabinol.secuboid.events;

import org.bukkit.event.HandlerList;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.playercontainer.PlayerContainer;

/**
 * The Class PlayerContainerAddNoEnterEvent. This events is called when a player container is added to a disallow inside
 * a land.
 */
public class PlayerContainerAddNoEnterEvent extends LandInsideEvent {

    /**
     * The Constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The player container.
     */
    private PlayerContainer playerContainer;

    /**
     * Instantiates a new player container add no enter events.
     *
     * @param land            the land
     * @param playerContainer the player container
     */
    public PlayerContainerAddNoEnterEvent(final Land land, final PlayerContainer playerContainer) {
        super(land);
        this.playerContainer = playerContainer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the player container.
     *
     * @return the player container
     */
    public PlayerContainer getPlayerContainer() {
        return playerContainer;
    }
}
