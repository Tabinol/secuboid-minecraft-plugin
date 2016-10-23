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
package me.tabinol.secuboid.events;

import me.tabinol.secuboid.lands.Land;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;


/**
 * The Class LandDeleteEvent. This events is called when a land is deleted.
 */
public class LandDeleteEvent extends LandEvent implements Cancellable {

    /** The Constant handlers. */
    private static final HandlerList handlers = new HandlerList();
    
    /** The events is cancelled. */
    protected boolean cancelled = false;

    /**
     * Instantiates a new land delete events.
     *
     * @param deletedLand the deleted land
     */
    public LandDeleteEvent(final Land deletedLand) {

        super(deletedLand);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.events.LandEvent#getHandlers()
     */

    /**
     *
     * @return
     */

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
    
    /* (non-Javadoc)
     * @see org.bukkit.events.Cancellable#isCancelled()
     */

    /**
     *
     * @return
     */

    public boolean isCancelled() {
        
        return cancelled;
    }

    /* (non-Javadoc)
     * @see org.bukkit.events.Cancellable#setCancelled(boolean)
     */

    /**
     *
     * @param bln
     */

    public void setCancelled(boolean bln) {
        
        cancelled = bln;
    }
}
