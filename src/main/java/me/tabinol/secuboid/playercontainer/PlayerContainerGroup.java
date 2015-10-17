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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainerGroup;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerGroup.
 */
public class PlayerContainerGroup extends PlayerContainer 
	implements IPlayerContainerGroup {
    
    /**
     * Instantiates a new player container group.
     *
     * @param groupName the group name
     */
    public PlayerContainerGroup(String groupName) {
        
        super(groupName, EPlayerContainerType.GROUP, true);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#equals(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerGroup &&
                name.equalsIgnoreCase(container2.getName());
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerGroup(name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        if(player != null) {
            return Secuboid.getThisPlugin().iDependPlugin().getPermission().playerInGroup(player, name);
        } else {
            return false;
        }
    }
    
    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {
        
        return ChatColor.BLUE + "G:" + ChatColor.WHITE + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {
        
    }
}
