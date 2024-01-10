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
import app.secuboid.api.recipients.RecipientExec;
import org.bukkit.event.HandlerList;

/**
 * This events is called when a player container is added to a disallow inside a
 * land.
 */
public class LandAddNoEnterEvent extends LandEvent {

    private static final HandlerList handlers = new HandlerList();

    private RecipientExec recipientExec;

    /**
     * Instantiates a new player container add no enter events.
     *
     * @param land          the land
     * @param recipientExec the recipientExec added
     */
    public LandAddNoEnterEvent(Land land, RecipientExec recipientExec) {
        super(land);
        this.recipientExec = recipientExec;
    }

    @SuppressWarnings("java:S4144")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the recipientExec added to no enter.
     *
     * @return the recipientExec
     */
    public RecipientExec getRecipient() {
        return recipientExec;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
