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
package me.tabinol.secuboidapi.event;

import me.tabinol.secuboidapi.lands.IDummyLand;
import me.tabinol.secuboidapi.lands.ILand;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * The Class LandEvent. Just for inheritance
 */
public class LandEvent extends Event {

    /** The Constant handlers. */
    private static final HandlerList handlers = new HandlerList();
    
    /** The dummy land. */
    private IDummyLand dummyLand;
    
    /** The land. */
    private ILand land;

    /**
     * Instantiates a new land event.
     *
     * @param dummyLand the dummy land
     */
    public LandEvent(IDummyLand dummyLand) {

        this.dummyLand = dummyLand;
        
        if(dummyLand instanceof ILand) {
            land = (ILand) dummyLand;
        } else {
            land = null;
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Event#getHandlers()
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

    /**
     * Gets the land.
     *
     * @return the land or null if the event is outside a land
     */
    public ILand getLand() {

        return land;
    }
    
    /**
     * Gets the land or outside area.
     *
     * @return the land or a "Dummy Land Word" if the event is outside a land
     */
    public IDummyLand getLandOrOutside() {
        
        return dummyLand;
    }
}
