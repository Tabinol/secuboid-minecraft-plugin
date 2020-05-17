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

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * The Class LandEvent. Just for inheritance
 */
public abstract class LandEvent extends Event {

    /**
     * The Constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The permissions land permissions flags (or world).
     */
    private final LandPermissionsFlags landPermissionsFlags;

    /**
     * Instantiates a new land events.
     *
     * @param landPermissionsFlags the land or world permissions flags
     */
    public LandEvent(LandPermissionsFlags landPermissionsFlags) {
        this.landPermissionsFlags = landPermissionsFlags;
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
     * Gets the land or outside area permissions flags.
     *
     * @return the land or a "Dummy Land Word" permissions flags if the events is
     *         outside a land
     */
    public LandPermissionsFlags getLandPermissionsFlags() {
        return landPermissionsFlags;
    }
}
