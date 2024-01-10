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
package app.secuboid.api.events;

import app.secuboid.api.lands.Land;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The Class LandEvent. Just for inheritance.
 */
public abstract class LandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Land land;

    /**
     * Instantiates a new land events.
     *
     * @param land the land
     */
    protected LandEvent(Land land) {
        this.land = land;
    }

    @SuppressWarnings("java:S4144")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {
        return land;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
