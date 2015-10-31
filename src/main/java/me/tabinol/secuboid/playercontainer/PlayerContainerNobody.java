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

import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerNobody;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerNobody.
 */
public class PlayerContainerNobody extends PlayerContainer implements ApiPlayerContainerNobody {

    /**
     * Instantiates a new player container nobody.
     */
    public PlayerContainerNobody() {
        
        super("", ApiPlayerContainerType.NOBODY, false);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#equals(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(ApiPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerNobody;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerNobody();
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        return false;
    }

    @Override
    public boolean hasAccess(Player player, ApiLand land) {
        
        return false;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(ApiLand land) {
        
    }
}
