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
package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.parameters.FlagList;

import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerOwner.
 */
public class PlayerContainerOwner extends PlayerContainer {

    /** The land. */
    private Land land;
    
    /**
     * Instantiates a new player container owner.
     *
     * @param land the land
     */
    public PlayerContainerOwner(Land land) {
        
        super("", PlayerContainerType.OWNER, false);
        this.land = land;
    }
    
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerOwner &&
                land == ((PlayerContainerOwner)container2).land;
    }

    public PlayerContainer copyOf() {
        
        return new PlayerContainerOwner(land);
    }

    public boolean hasAccess(Player player) {
        
        return hasAccess(player, land);
    }
        
    public boolean hasAccess(Player player, Land land) {
        
        boolean value;
        Land parent;
        
        if(land == null) {
            return false;
        }
        
        value = land.getOwner().hasAccess(player);
        
        if(!value && (parent = land.getParent()) != null 
                && land.getFlagAndInherit(FlagList.INHERIT_OWNER.getFlagType()).getValueBoolean() == true) {
            
            return parent.getOwner().hasAccess(player);
        }
        
        return value;
    }


    /**
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {
        
        return land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(Land land) {

        this.land = land;
    }
}
