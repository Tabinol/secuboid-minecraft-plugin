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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;


/**
 * The Class PlayerLandChangeEvent. This event is called every time a player
 * moves from a land to an other, or to an other world.
 */
public class PlayerLandChangeEvent extends LandEvent implements Cancellable {
    
    /** The Constant handlers. */
    private static final HandlerList handlers = new HandlerList();
    
    /** The cancelled. */
    protected boolean cancelled = false;
    
    /** The player. */
    Player player;
    
    /** The from loc. */
    Location fromLoc;
    
    /** The to loc. */
    Location toLoc;
    
    /** The last land. */
    ILand lastLand;
    
    /** The last dummy land. */
    IDummyLand lastDummyLand;
    
    /** The if this is a player teleport. */
    boolean isTp;

    /**
     * Instantiates a new player land change event.
     *
     * @param lastDummyLand the last dummy land
     * @param dummyLand the actual dummy land
     * @param player the player
     * @param fromLoc from location
     * @param toLoc the to location
     * @param isTp the is a player teleport
     */
    public PlayerLandChangeEvent(final IDummyLand lastDummyLand, final IDummyLand dummyLand, final Player player, 
            final Location fromLoc, final Location toLoc, final boolean isTp) {

        super(dummyLand);
        this.lastDummyLand = lastDummyLand;
        
        if(lastDummyLand instanceof ILand) {
            lastLand = (ILand) lastDummyLand;
        } else {
            lastLand = null;
        }
        
        this.player = player;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.isTp = isTp;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.event.LandEvent#getHandlers()
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
     * @see org.bukkit.event.Cancellable#isCancelled()
     */
    public boolean isCancelled() {
        
        return cancelled;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    public void setCancelled(boolean bln) {
        
        cancelled = bln;
    }
    
    /**
     * Gets the player.
     *
     * @return the player
     */
    public Player getPlayer() {
        
        return player;
    }
    
    /**
     * Gets the last land.
     *
     * @return the last land
     */
    public ILand getLastLand() {
        
        return lastLand;
    }
    
    /**
     * Gets the last land or outside area (World).
     *
     * @return the last land or dummy land (World)
     */
    public IDummyLand getLastLandOrOutside() {
        
        return lastDummyLand;
    }
    
    /**
     * Gets the from location.
     *
     * @return the from location
     */
    public Location getFromLoc() {
        
        return fromLoc;
    }

    /**
     * Gets the to location.
     *
     * @return the to location
     */
    public Location getToLoc() {
        
        return toLoc;
    }
    
    /**
     * Checks if this is a player teleport.
     *
     * @return true, if it is a player teleport
     */
    public boolean isTp() {
        
        return isTp;
    }
}
