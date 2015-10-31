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

import me.tabinol.secuboid.parameters.FlagList;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerResident;

import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerResident.
 */
public class PlayerContainerResident extends PlayerContainer
    implements ApiPlayerContainerResident {
    
    /** The land. */
    private ApiLand land;
    
    /**
     * Instantiates a new player container resident.
     *
     * @param land the land
     */
    public PlayerContainerResident(ApiLand land) {
        
        super("", ApiPlayerContainerType.RESIDENT, false);
        this.land = land;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#equals(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(ApiPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerResident &&
                land == ((PlayerContainerResident)container2).land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerResident(land);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        return hasAccess(player, land);
    }
    
    @Override
    public boolean hasAccess(Player player, ApiLand land) {
        
        if(land == null) {
            return false;
        }
        
        boolean value = land.isResident(player);
        ApiLand actual = land;
        ApiLand parent;

        while(!value && (parent = actual.getParent()) != null 
                && actual.getFlagAndInherit(FlagList.INHERIT_RESIDENTS.getFlagType()).getValueBoolean() == true) {
            
            value = parent.isResident(player);
            actual = parent;
        }
        
        return value;
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public ApiLand getLand() {
        
        return land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(ApiLand land) {
        
        this.land = land;
    }
}
