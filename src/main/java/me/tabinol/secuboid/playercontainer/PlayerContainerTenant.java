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
 * The Class PlayerContainerTenant.
 */
public class PlayerContainerTenant extends PlayerContainer {
    
    /** The land. */
    private Land land;
    
    /**
     * Instantiates a new player container tenant.
     *
     * @param land the land
     */
    public PlayerContainerTenant(Land land) {
        
        super("", PlayerContainerType.TENANT, false);
        this.land = land;
    }
    
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerTenant &&
                land == ((PlayerContainerTenant)container2).land;
    }

    public PlayerContainer copyOf() {
        
        return new PlayerContainerTenant(land);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        return hasAccess(player, land);
    }
    
    @Override
    public boolean hasAccess(Player player, Land land) {

        if(land == null) {
            return false;
        }

        boolean value = land.isTenant(player);
        Land actual = land;
        Land parent;

        while(!value && (parent = actual.getParent()) != null 
                && actual.getFlagAndInherit(FlagList.INHERIT_RESIDENTS.getFlagType()).getValueBoolean() == true) {
            
            value = parent.isTenant(player);
            actual = parent;
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
